package fr.inria.hellobranchf;

import java.util.ArrayList;
import java.util.List;

public class AppBranch {
    public static void myIf(boolean c) {
        if(c) {
            System.out.println("if true");
            //getInstance().m("a","b","c");
        }
    }
    public static void myIfElse(boolean c) {
        if(c) {
            System.out.println("ifelse true");
        } else {
            System.out.println("ifelse false");
        }
    }

    public static void myIfElse(boolean c1, boolean c2) {
        if(c1) {
            if(c2) {
                System.out.println("ifelseImb true true");
            } else {
                System.out.println("ifelseImb true false");
            }
        } else {
            if(c2) {
                System.out.println("ifelseImb false true");
            } else {
                System.out.println("ifelseImb false false");
            }
        }
    }

    public static void mySwitch(int i) {
        //getInstance().m("a","b","c");
        System.out.println("myswitch");
        switch (i) {
            case 1:
                System.out.println("switch 1");
                break;
            case 2:
                System.out.println("switch 2");
                break;
            //default:
            //    System.out.println("switch default");
        }
        return;
    }

    public static void myFor() {
        for(int j = 0; j < 2; j++) {
            System.out.println("for loop " + j);
        }
    }

    public static void myWhile() {
        int k = 0;
        while(k < 2) {
            System.out.println("while loop " + k);
            k++;
        }
    }

    public static void myDoWhile() {
        int l = 0;
        do {
            System.out.println("do while loop " + l);
            l++;
        } while(l < 2);

    }

    public static void myForeach() {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        for(Integer m : list) {
            System.out.println("foreach loop " + m);
        }
    }

    public static void myTry() {
        try {
            throw new Exception("My Exception");
        } catch (Exception e) {
            System.out.println("MyCatch of " + e.getMessage());
        }
    }

    /*public static AppBranch getInstance() {return new AppBranch();}
    public void m(String a, String b, String c) {
        System.out.println(a + b + c);
    }*/

    public static void main( String[] args ) {
        System.out.println("Hello world!");

        //PrintCol p = new PrintCol();
        //p.print();
        //A a = new A();
        //a.test();

        myIf(true);
        myIf(false);
        myIfElse(true);
        myIfElse(false);
        myIfElse(true, true);
        myIfElse(true, false);
        myIfElse(false, true);
        myIfElse(false, false);
        mySwitch(1);
        mySwitch(2);
        mySwitch(3);
        myFor();
        myWhile();
        myDoWhile();
        myForeach();
        myTry();
    }
}
