package se.kth.castor.yajta.processor.loggers;

import com.google.common.collect.BiMap;
import se.kth.castor.align.treediff.FastRemoteReader;
import se.kth.castor.offline.FastRemoteReaderTest;
import se.kth.castor.yajta.processor.IdTreeNode;
import se.kth.castor.yajta.processor.util.MyEntry;
import se.kth.castor.yajta.processor.util.MyMap;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FastFollowerTest {
	@Test
	public void testReadTrace() throws InterruptedException {
		File traceDir = new File(FastRemoteReaderTest.class.getClassLoader().getResource("remote").getPath());
		FastLogger.getInstance().clear();
		FastRemoteReader reader = new FastRemoteReader(FastLogger.getInstance(),traceDir);

		//Reconstitute logs
		reader.read();

		//Feed traces to FastTie
		MyMap<Long, IdTreeNode> oldLogs = FastLogger.getInstance().exportLogs();
		BiMap<String, Integer> oldDico = FastLogger.getInstance().getDictionary();

		FastFollower follower = new FastFollower();
		MyMap<Long, Boolean> threadOfftrack = new MyMap<>();
		for(Long t: oldLogs.keyList()) {
			threadOfftrack.put(t,false);
		}
		follower.load(oldLogs, threadOfftrack, oldDico);


		for(MyEntry<Long,IdTreeNode> e: oldLogs.entryList()) {
			//skip container node
			IdTreeNode node = e.getValue().children.get(0);

			//contract: When following an identical trace, no offtrack is raised
			visit(follower, node, e.getKey());
			assertFalse(follower.threadOfftrack.get(e.getKey()));
		}

	}

	@Test
	public void testDivergentTrace() throws InterruptedException {
		File traceDir = new File(FastRemoteReaderTest.class.getClassLoader().getResource("remote").getPath());
		FastLogger.getInstance().clear();
		FastRemoteReader reader = new FastRemoteReader(FastLogger.getInstance(),traceDir);

		//Reconstitute logs
		reader.read();

		//Feed traces to FastTie
		MyMap<Long, IdTreeNode> oldLogs = FastLogger.getInstance().exportLogs();
		BiMap<String, Integer> oldDico = FastLogger.getInstance().getDictionary();

		FastFollower follower = new FastFollower();
		MyMap<Long, Boolean> threadOfftrack = new MyMap<>();
		for(Long t: oldLogs.keyList()) {
			threadOfftrack.put(t,false);
		}
		follower.load(oldLogs, threadOfftrack, oldDico);


		for(MyEntry<Long,IdTreeNode> e: oldLogs.entryList()) {
			//skip container node
			IdTreeNode node = e.getValue().children.get(0);

			//contract: When following an divergent trace, offtrack is raised
			//Here we visit twice
			visit(follower, node, e.getKey());
			visit(follower, node, e.getKey());

			assertTrue(follower.threadOfftrack.get(e.getKey()));
		}

	}

	public void visit(FastFollower f, IdTreeNode node, long thread) {
		f.stepIn(thread, node.id);
		if(node.children != null) {
			for (IdTreeNode child : node.children) {
				visit(f, child, thread);
			}
		}
		f.stepOut(thread);
	}

}