package fr.inria.yajta.processor.loggers;

import com.google.common.collect.BiMap;
import fr.inria.align.treediff.FastRemoteReader;
import fr.inria.align.treediff.FastRemoteReaderTest;
import fr.inria.yajta.api.loggerimplem.TestFastLogger;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class FastTieTest {
	@Test
	public void testReadTrace() throws InterruptedException {
		File traceDir = new File(FastRemoteReaderTest.class.getClassLoader().getResource("remote").getPath());
		FastTie.getInstance().visited.clear();
		FastRemoteReader reader = new FastRemoteReader(FastTie.getInstance(),traceDir);

		//Reconstitute logs
		reader.read();

		//Feed traces to FastTie
		Set<Integer> logs = FastTie.getInstance().visited;
		BiMap<Integer, String> dico = FastTie.getInstance().getDico().inverse();


		//contract: Every method and each branch is indeed logged (in and out)
		assertTrue(logs.size() == 47);

		//contract: As we only log each distinct method once, dico should have the same size as log
		assertEquals(logs.size(), dico.size());

	}

}