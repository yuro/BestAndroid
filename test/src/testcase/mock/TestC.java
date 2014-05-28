package testcase.mock;

public class TestC {
    private static TestC mTestC = new TestC();

    public static TestC getInstance() {
        return mTestC;
    }

    public int getValue(int value) {
        return value;
    }
}
