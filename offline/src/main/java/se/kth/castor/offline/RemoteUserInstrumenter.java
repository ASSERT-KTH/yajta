package se.kth.castor.offline;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import se.kth.castor.yajta.api.ClassList;
import se.kth.castor.yajta.api.MalformedTrackingClassException;
import se.kth.castor.yajta.processor.loggers.RemoteUserLogger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;

public class RemoteUserInstrumenter {

	@Parameter(names = {"--help", "-h"}, help = true, description = "Display this message.")
	private boolean help;
	@Parameter(names = {"--in-class-dir", "-i"}, description = "Directory containing bytecode to instrument, or jar to instrument")
	private String classDir;
	@Parameter(names = {"--out-class-dir", "-o"}, description = "Directory in which to output instrumented bytecode. Default: inst-classes")
	private String traceDir = "./";
	@Parameter(names = {"--add-yajta-classes", "-y"}, description = "Copy Yajta classes to ouput dir. Default: false")
	private boolean addYajtaClassesToOutput = false;

	static String tmpInDirName = "yajta-tmp-in";
	static String tmpOutDirName = "yajta-tmp-out";

	public static void printUsage(JCommander jcom) {
		jcom.usage();
	}

	public static void main(String args[]) throws MalformedTrackingClassException, IOException, URISyntaxException {
		RemoteUserInstrumenter r = new RemoteUserInstrumenter();
		JCommander jcom = new JCommander(r,args);
		if(r.help || r.classDir == null) {
			printUsage(jcom);
		} else {
			File iDir = new File(r.classDir);
			File oDir = new File(r.traceDir);
			ClassList cl = new ClassList(new String[0],new String[0],null,false);
			InstrumentationBuilder ib;
			File manifest = null;
			if(iDir.getName().endsWith(".jar")) {
				//
				File tmpInDir = new File(oDir, tmpInDirName);
				if(tmpInDir.exists()) tmpInDir.delete();
				tmpInDir.mkdirs();

				File tmpOutDir = new File(oDir, tmpOutDirName);
				if(tmpOutDir.exists()) tmpOutDir.delete();
				tmpOutDir.mkdirs();
				//manifest = decompressJar(iDir, tmpDir);
				decompressJar(iDir, tmpInDir, true);
				copyNonClassFile(tmpInDir.toPath(), tmpOutDir.toPath(), StandardCopyOption.REPLACE_EXISTING);

				ib = new InstrumentationBuilder(tmpInDir, tmpOutDir, cl, RemoteUserLogger.class, true);
			} else {
				ib = new InstrumentationBuilder(iDir, oDir, cl, RemoteUserLogger.class, true);
			}
			//InstrumentationBuilder ib = new InstrumentationBuilder(iDir,oDir,cl,RemoteLogger.class, true);
			ib.instrument();

			if(iDir.isFile()) {
				String name = iDir.getName();

				File tmpDir = new File(oDir, tmpOutDirName);


				if(r.addYajtaClassesToOutput) {
					File yajtaJar = new File(
							RemoteUserInstrumenter.class
									.getProtectionDomain()
									.getCodeSource()
									.getLocation().toURI()
					);
					if(!yajtaJar.getName().endsWith(".jar")) {
						System.err.println("Warning Yajta full dependencies can only be added if ran from its jar.");
					}
					decompressJar(yajtaJar, tmpDir, false);
				}

				createJar(new File(oDir, name), tmpDir);
				//createJar(new File(oDir, name), tmpDir, new Manifest(new FileInputStream(manifest)));
				if(tmpDir.exists()) tmpDir.delete();
			}
			System.out.println("Instrumentation Done.");
		}
	}

	public static void copyNonClassFile(Path source, Path target, CopyOption... options)
			throws IOException {
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
					throws IOException {
				createDirectories(target.resolve(source.relativize(dir)));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					throws IOException {
				if(!file.toString().endsWith(".class")) {
					System.out.println("copy " + file);
					copy(file, target.resolve(source.relativize(file)), options);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void decompressJar(File iJar, File tmpDir, boolean override) {
		try {
			JarFile jar = new JarFile(iJar);
			Enumeration enumEntries = jar.entries();
			while (enumEntries.hasMoreElements()) {
				JarEntry file = (JarEntry) enumEntries.nextElement();
				File f;
				/*if(file.getName().endsWith("MANIFEST.MF")) {
					f = manifest;
				} else {*/
				if (file.isDirectory()) continue;
       final File zipEntryFile = new File(tmpDir,file.getName());
       if(!zipEntryFile.toPath().normalize().startsWith(tmpDir.toPath().normalize())) {
           throw new IOException("Bad zip entry");
       }
       f = zipEntryFile;
				if (!f.getParentFile().exists()) { // if its a directory, create it
					f.getParentFile().mkdirs();
				}
				if(f.exists() && !override) continue;
				//}
				InputStream is = jar.getInputStream(file);
				FileOutputStream fos = new FileOutputStream(f);
				while (is.available() > 0) {  // write contents of 'is' to 'fos'
					fos.write(is.read());
				}
				fos.close();
				is.close();
			}
			jar.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//return manifest;
	}

	public static void createJar(File oJar, File dir) {
		try {
			JarOutputStream target = new JarOutputStream(new FileOutputStream(oJar));
			addToJar(dir, target, dir.getAbsolutePath() + "/");
			target.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void addToJar(File source, JarOutputStream target, String prepath) throws IOException {
		BufferedInputStream in = null;
		try {
			if (source.isDirectory()) {
				String name = source.getPath().replace("\\", "/").replace(prepath, "");
				if (!name.isEmpty()) {
					if (!name.endsWith("/"))
						name += "/";
					JarEntry entry = new JarEntry(name);
					entry.setTime(source.lastModified());
					target.putNextEntry(entry);
					target.closeEntry();
				}
				for (File nestedFile: source.listFiles())
					addToJar(nestedFile, target, prepath);
				return;
			}

			String ePath = source.getAbsolutePath().replace("\\", "/").replace(prepath, "");
			System.out.println("Add to jar: " + ePath + " (prepath " + prepath + ")");
			JarEntry entry = new JarEntry(ePath);
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(new FileInputStream(source));

			byte[] buffer = new byte[1024];
			while (true) {
				int count = in.read(buffer);
				if (count == -1)
					break;
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		} finally {
			if (in != null)
				in.close();
		}
	}
}
