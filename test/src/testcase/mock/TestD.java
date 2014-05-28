package testcase.mock;

public class TestD {
    public int getValue(int value) {
        return TestC.getInstance().getValue(value);
    }
}
