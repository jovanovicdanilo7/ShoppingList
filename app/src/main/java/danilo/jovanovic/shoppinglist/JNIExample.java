package danilo.jovanovic.shoppinglist;

public class JNIExample {

    static{
        System.loadLibrary("MyLibrary");
    }

    public native int increment(int x);
}


