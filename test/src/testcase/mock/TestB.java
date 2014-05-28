package testcase.mock;

public class TestB {
    public int getValue(TestA a) {
        return a.getValue();
    }

    public int getValueInter() {
        TestA a = new TestA();
        return a.getValue();
    }
}
