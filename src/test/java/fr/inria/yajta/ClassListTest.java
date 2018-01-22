package fr.inria.yajta;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ClassListTest {

    private String[] includes;
    private String[] excludes;
    private String[] isotopes;
    private boolean strictIncludes;

    private String[] ok;
    private String[] notOk;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { new String[]{"org","org/p1/sp1","org/p2/Class"},
                        new String[]{"org/p1","org/p2"},
                        new String[]{},
                        false,
                        new String[]{"org/Class","org/p1/sp1/Class","org/p2/Class", "com/Class"},
                        new String[]{"org/p1/Class","org/p1/sp2/Class","org/p2/Class2","fr/inria/yajta/Agent"}
                        },
                { new String[]{"org","org/p1/sp1","org/p2/Class"},
                        new String[]{"org/p1","org/p2"},
                        new String[]{},
                        true,
                        new String[]{"org/Class","org/p1/sp1/Class","org/p2/Class"},
                        new String[]{"org/p1/Class","org/p1/sp2/Class","org/p2/Class2", "com/Class","fr/inria/yajta/Agent"}
                },
                { null,
                        new String[]{"org/p1","org/p2"},
                        new String[]{},
                        true,
                        new String[]{},
                        new String[]{"org/Class","org/p1/sp1/Class","org/p2/Class","org/p1/Class","org/p1/sp2/Class","org/p2/Class2", "com/Class","fr/inria/yajta/Agent"}
                },
                { new String[]{"org","org/p1/sp1","org/p2/Class"},
                        null,
                        new String[]{},
                        true,
                        new String[]{"org/Class","org/p1/sp1/Class","org/p2/Class","org/p1/Class","org/p1/sp2/Class","org/p2/Class2"},
                        new String[]{"com/Class","fr/inria/yajta/Agent"}
                },
                { null,
                        null,
                        new String[]{},
                        true,
                        new String[]{},
                        new String[]{"org/Class","org/p1/sp1/Class","org/p2/Class","org/p1/Class","org/p1/sp2/Class","org/p2/Class2", "com/Class","fr/inria/yajta/Agent"}
                },
                { null,
                        new String[]{"org/p1","org/p2"},
                        new String[]{},
                        false,
                        new String[]{"org/Class", "com/Class"},
                        new String[]{"org/p1/Class","org/p1/sp2/Class","org/p2/Class2","org/p1/sp1/Class","org/p2/Class","fr/inria/yajta/Agent"}
                },
                { new String[]{"org","org/p1/sp1","org/p2/Class"},
                        null,
                        new String[]{},
                        false,
                        new String[]{"org/Class","org/p1/sp1/Class","org/p2/Class","org/p1/Class","org/p1/sp2/Class","org/p2/Class2", "com/Class"},
                        new String[]{"fr/inria/yajta/Agent"}
                },
                { null,
                        null,
                        new String[]{},
                        false,
                        new String[]{"org/Class","org/p1/sp1/Class","org/p2/Class","org/p1/Class","org/p1/sp2/Class","org/p2/Class2", "com/Class"},
                        new String[]{"fr/inria/yajta/Agent"}
                }
        });
    }

    public ClassListTest(String[] includes, String[] excludes, String[] isotopes, boolean strictIncludes, String[] ok, String[] notOk) {
        this.includes = includes;
        this.excludes = excludes;
        this.isotopes = isotopes;
        this.strictIncludes = strictIncludes;

        this.ok = ok;
        this.notOk = notOk;
    }

    @org.junit.Test
    public void isToBeProcessed() throws Exception {
        ClassList cl = new ClassList(includes,excludes,isotopes,strictIncludes);

        for(int i = 0; i < ok.length; i++) {
            assertTrue("Class " + ok[i] + " should be ok", cl.isToBeProcessed(ok[i]));
        }

        for(int i = 0; i < notOk.length; i++) {
            assertFalse("Class " + notOk[i] + " should NOT be ok", cl.isToBeProcessed(notOk[i]));
        }
    }

}