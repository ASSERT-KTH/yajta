package fr.inria.yajta.api;

import java.io.File;

public interface BranchTracking {

    void branchIn(String thread, String branch);
    void branchOut(String thread);
}
