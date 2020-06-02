package se.kth.castor.testentry;

public class AppValue {
    public static int getI(int i) {
        return i+1;
    }
    public static void main( String[] args ) {
        System.out.println("hello world! " + getI(3));
    }
}
