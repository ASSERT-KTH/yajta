package se.kth.castor.yajta;

import se.kth.castor.yajta.processor.util.MyMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static boolean startWith(String str, String[] ins) {
        for( String in : ins ) {
            if( str.startsWith(in) )
                return true;
        }
        return false;
    }

    public static String[] format(String[] ar) {
        if(ar != null) {
            String[] res = new String[ar.length];
            if(ar.length > 0) {
                for(int i = 0; i < ar.length; i++) res[i] = ar[i].replace(".","/");
            }
            return res;
        } else {
            return null;
        }
    }

    public static String[] listClassesAsArray(File f) {
        Object[] classNamesO = listClasses(f).toArray();
        String[] classNames = new String[classNamesO.length];
        for(int i = 0; i < classNamesO.length; i++) classNames[i] = ((String) classNamesO[i]);
        return classNames;
    }

    public static List<String> listClasses(File f) {
        return listClassesExecutor(f,f);
    }

    public static List<String> listClassesExecutor(File f, File root) {
        List<String> res = new ArrayList<>();
        if(f.isDirectory()) {
            for(File c: f.listFiles()) {
                res.addAll(listClassesExecutor(c, root));
            }
        } else if (f.getName().endsWith(".class")
                && !(f.getName().endsWith("package-info.class") || f.getName().endsWith("module-info.class"))) {
            res.add(
                    f.getPath()
                            .split("\\.class")[0]
                            .substring(root.getPath().length()+1)
                            .replace("/",".")
            );
        }
        return res;
    }

    public static File getATmpDir() {
        int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
        File tmp = new File(System.getProperty("java.io.tmpdir"));
        File dir = new File(tmp,"tmp" + i);
        dir.mkdir();
        return dir;
    }

    static MyMap<Integer,String> opcode;

    /**
     * @param code of the opcode
     * @return the string name of the corresponding opcode
     */
    public static String getOpcode(int code) {
        if(opcode == null) {
            opcode = new MyMap<>();
            opcode.put(0, "nop");
            opcode.put(1, "aconst_null");
            opcode.put(2, "iconst_m1");
            opcode.put(3, "iconst_0");
            opcode.put(4, "iconst_1");
            opcode.put(5, "iconst_2");
            opcode.put(6, "iconst_3");
            opcode.put(7, "iconst_4");
            opcode.put(8, "iconst_5");
            opcode.put(9, "lconst_0");
            opcode.put(10, "lconst_1");
            opcode.put(11, "fconst_0");
            opcode.put(12, "fconst_1");
            opcode.put(13, "fconst_2");
            opcode.put(14, "dconst_0");
            opcode.put(15, "dconst_1");
            opcode.put(16, "bipush");
            opcode.put(17, "sipush");
            opcode.put(18, "ldc");
            opcode.put(19, "ldc_w");
            opcode.put(20, "ldc2_w");
            opcode.put(21, "iload");
            opcode.put(22, "lload");
            opcode.put(23, "fload");
            opcode.put(24, "dload");
            opcode.put(25, "aload");
            opcode.put(26, "iload_0");
            opcode.put(27, "iload_1");
            opcode.put(28, "iload_2");
            opcode.put(29, "iload_3");
            opcode.put(30, "lload_0");
            opcode.put(31, "lload_1");
            opcode.put(32, "lload_2");
            opcode.put(33, "lload_3");
            opcode.put(34, "fload_0");
            opcode.put(35, "fload_1");
            opcode.put(36, "fload_2");
            opcode.put(37, "fload_3");
            opcode.put(38, "dload_0");
            opcode.put(39, "dload_1");
            opcode.put(40, "dload_2");
            opcode.put(41, "dload_3");
            opcode.put(42, "aload_0");
            opcode.put(43, "aload_1");
            opcode.put(44, "aload_2");
            opcode.put(45, "aload_3");
            opcode.put(46, "iaload");
            opcode.put(47, "laload");
            opcode.put(48, "faload");
            opcode.put(49, "daload");
            opcode.put(50, "aaload");
            opcode.put(51, "baload");
            opcode.put(52, "caload");
            opcode.put(53, "saload");
            opcode.put(54, "istore");
            opcode.put(55, "lstore");
            opcode.put(56, "fstore");
            opcode.put(57, "dstore");
            opcode.put(58, "astore");
            opcode.put(59, "istore_0");
            opcode.put(60, "istore_1");
            opcode.put(61, "istore_2");
            opcode.put(62, "istore_3");
            opcode.put(63, "lstore_0");
            opcode.put(64, "lstore_1");
            opcode.put(65, "lstore_2");
            opcode.put(66, "lstore_3");
            opcode.put(67, "fstore_0");
            opcode.put(68, "fstore_1");
            opcode.put(69, "fstore_2");
            opcode.put(70, "fstore_3");
            opcode.put(71, "dstore_0");
            opcode.put(72, "dstore_1");
            opcode.put(73, "dstore_2");
            opcode.put(74, "dstore_3");
            opcode.put(75, "astore_0");
            opcode.put(76, "astore_1");
            opcode.put(77, "astore_2");
            opcode.put(78, "astore_3");
            opcode.put(79, "iastore");
            opcode.put(80, "lastore");
            opcode.put(81, "fastore");
            opcode.put(82, "dastore");
            opcode.put(83, "aastore");
            opcode.put(84, "bastore");
            opcode.put(85, "castore");
            opcode.put(86, "sastore");
            opcode.put(87, "pop");
            opcode.put(88, "pop2");
            opcode.put(89, "dup");
            opcode.put(90, "dup_x1");
            opcode.put(91, "dup_x2");
            opcode.put(92, "dup2");
            opcode.put(93, "dup2_x1");
            opcode.put(94, "dup2_x2");
            opcode.put(95, "swap");
            opcode.put(96, "iadd");
            opcode.put(97, "ladd");
            opcode.put(98, "fadd");
            opcode.put(99, "dadd");
            opcode.put(100, "isub");
            opcode.put(101, "lsub");
            opcode.put(102, "fsub");
            opcode.put(103, "dsub");
            opcode.put(104, "imul");
            opcode.put(105, "lmul");
            opcode.put(106, "fmul");
            opcode.put(107, "dmul");
            opcode.put(108, "idiv");
            opcode.put(109, "ldiv");
            opcode.put(110, "fdiv");
            opcode.put(111, "ddiv");
            opcode.put(112, "irem");
            opcode.put(113, "lrem");
            opcode.put(114, "frem");
            opcode.put(115, "drem");
            opcode.put(116, "ineg");
            opcode.put(117, "lneg");
            opcode.put(118, "fneg");
            opcode.put(119, "dneg");
            opcode.put(120, "ishl");
            opcode.put(121, "lshl");
            opcode.put(122, "ishr");
            opcode.put(123, "lshr");
            opcode.put(124, "iushr");
            opcode.put(125, "lushr");
            opcode.put(126, "iand");
            opcode.put(127, "land");
            opcode.put(128, "ior");
            opcode.put(129, "lor");
            opcode.put(130, "ixor");
            opcode.put(131, "lxor");
            opcode.put(132, "iinc");
            opcode.put(133, "i2l");
            opcode.put(134, "i2f");
            opcode.put(135, "i2d");
            opcode.put(136, "l2i");
            opcode.put(137, "l2f");
            opcode.put(138, "l2d");
            opcode.put(139, "f2i");
            opcode.put(140, "f2l");
            opcode.put(141, "f2d");
            opcode.put(142, "d2i");
            opcode.put(143, "d2l");
            opcode.put(144, "d2f");
            opcode.put(145, "i2b");
            opcode.put(146, "i2c");
            opcode.put(147, "i2s");
            opcode.put(148, "lcmp");
            opcode.put(149, "fcmpl");
            opcode.put(150, "fcmpg");
            opcode.put(151, "dcmpl");
            opcode.put(152, "dcmpg");
            opcode.put(153, "ifeq");
            opcode.put(154, "ifne");
            opcode.put(155, "iflt");
            opcode.put(156, "ifge");
            opcode.put(157, "ifgt");
            opcode.put(158, "ifle");
            opcode.put(159, "if_icmpeq");
            opcode.put(160, "if_icmpne");
            opcode.put(161, "if_icmplt");
            opcode.put(162, "if_icmpge");
            opcode.put(163, "if_icmpgt");
            opcode.put(164, "if_icmple");
            opcode.put(165, "if_acmpeq");
            opcode.put(166, "if_acmpne");
            opcode.put(167, "goto");
            opcode.put(168, "jsr");
            opcode.put(169, "ret");
            opcode.put(170, "tableswitch");
            opcode.put(171, "lookupswitch");
            opcode.put(172, "ireturn");
            opcode.put(173, "lreturn");
            opcode.put(174, "freturn");
            opcode.put(175, "dreturn");
            opcode.put(176, "areturn");
            opcode.put(177, "return");
            opcode.put(178, "getstatic");
            opcode.put(179, "putstatic");
            opcode.put(180, "getfield");
            opcode.put(181, "putfield");
            opcode.put(182, "invokevirtual");
            opcode.put(183, "invokespecial");
            opcode.put(184, "invokestatic");
            opcode.put(185, "invokeinterface");
            opcode.put(186, "invokedynamic");
            opcode.put(187, "new");
            opcode.put(188, "newarray");
            opcode.put(189, "anewarray");
            opcode.put(190, "arraylength");
            opcode.put(191, "athrow");
            opcode.put(192, "checkcast");
            opcode.put(193, "instanceof");
            opcode.put(194, "monitorenter");
            opcode.put(195, "monitorexit");
            opcode.put(196, "wide");
            opcode.put(197, "multianewarray");
            opcode.put(198, "ifnull");
            opcode.put(199, "ifnonnull");
            opcode.put(200, "goto_w");
            opcode.put(201, "jsr_w");
            opcode.put(202, "breakpoint");
            opcode.put(254, "impdep1");
            opcode.put(255, "impdep2");
        }
        if(opcode.containsKey(code)) return opcode.get(code);
        else return "NOT_FOUND";
    }

}
