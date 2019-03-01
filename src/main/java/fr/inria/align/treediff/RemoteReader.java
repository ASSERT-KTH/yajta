package fr.inria.align.treediff;

import fr.inria.yajta.api.BranchTracking;
import fr.inria.yajta.api.Tracking;
import fr.inria.yajta.api.ValueTracking;
import fr.inria.yajta.processor.loggers.Logger;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import java.io.File;

public class RemoteReader {

    public static void main(String arg[]) throws InterruptedException {
        RemoteReader r = new RemoteReader();
        r.logger = Logger.getInstance();
        File dd = new File("/home/nharrand/Documents/helloworld");
        r.dir = new File(dd,"d");
        r.loggerB = (Logger) r.logger;
        r.logger.setLogFile(new File("out.json"));
        r.read();
        r.logger.flush();
    }


    Tracking logger;
    BranchTracking loggerB;
    ValueTracking loggerV;
    File dir;
    boolean done = false;
    public void read() throws InterruptedException {

        try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir.getAbsolutePath() + "/trace").build()) {
            final ExcerptTailer tailer = queue.createTailer();
            while(!done) {
                tailer.readDocument(w -> {
                    w.read("trace").marshallable(
                        m -> {
                            String type = m.read("type").text();
                            System.err.println("t:" + type);
                            switch (type) {
                                case "stepIn":
                                    logger.stepIn(m.read("thread").text(),
                                            m.read("clazz").text(),
                                            m.read("method").text());
                                    break;
                                case "stepOut":
                                    logger.stepOut(m.read("thread").text());

                                    break;
                                case "branchIn":
                                    loggerB.branchIn(m.read("thread").text(),
                                            m.read("branch").text());
                                    break;
                                case "branchOut":
                                    loggerB.branchOut(m.read("thread").text());
                                    break;
                                case "stepInV":

                                    break;
                                case "stepOutV":

                                    break;
                                case "end":
                                    end();
                                    break;
                                default:

                                    break;
                            }
                        });
                });
                Thread.sleep(10);
            }

        }
    }

    public void end() {
        done = true;
    }
}
