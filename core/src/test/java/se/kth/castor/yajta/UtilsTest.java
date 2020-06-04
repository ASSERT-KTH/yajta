package se.kth.castor.yajta;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

public class UtilsTest {

	@Test
	public void startWith() {
		String prefixes[] = new String[] {"org","com","java.util"};
		String in[] = new String[] {"java.util.List","org.apache","com."};
		String out[] = new String[] {"se.kth.castor","se.kth","java.lang"};

		for(String s: in) {
			assertTrue(Utils.startWith(s,prefixes));
		}

		for(String s: out) {
			assertFalse(Utils.startWith(s,prefixes));
		}
	}

	@Test
	public void format() {
		String out[] = new String[] {"java/util/List","org/apache","com/"};
		String in[] = new String[] {"java.util.List","org.apache","com."};

		//contract: qualified name following bytecode standard are translated into java source standard
		assertEquals(Utils.format(in), out);
	}
	@Test
	public void getOpcode() {
		//contract: opcode are translated into human readable forms
		assertEquals(Utils.getOpcode(4), "iconst_1");
		assertEquals(Utils.getOpcode(16), "bipush");
		assertEquals(Utils.getOpcode(29), "iload_3");
	}

	@Test
	public void testListClasses() {
		Class cl= UtilsTest.class;
		ClassLoader cll= cl.getClassLoader();
		URL url = cll.getResource("diversefiles");
		String path = url.getFile();
		System.out.println(path);
		File dir = new File(path);
		List<String> classes = Utils.listClasses(dir);

		assertEquals(1, classes.size());
	}
}