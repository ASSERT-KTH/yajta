package fr.inria.yajta.api;

import java.io.File;

public interface BranchTracking {

    void branchIn(String thread, String branch);

    //Not implemented: Tracer won't insert this probe.
    //Should be removed? (Can't really get out of one branch
    // without either exiting the method or entering a new one...
    void branchOut(String thread);
}
