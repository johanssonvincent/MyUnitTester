import se.umu.cs.unittest.TestClass;

public class TestNull implements TestClass {
    private MyInt myInt;

    public TestNull() {
    }

    public void setUp() {
        myInt=new MyInt();
    }

    public void tearDown() {
        myInt=null;
    }

    //Test that should fail
    public boolean testFailingByException() {
        myInt=null;
        myInt.decrement();
        return true;
    }
}