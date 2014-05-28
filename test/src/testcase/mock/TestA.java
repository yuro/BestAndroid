package testcase.mock;

public class TestA {
    public static int mValue1 = 0;

    public int getValue() {
        return mValue1;
    }

    private void setValue(int value) {
        mValue1 = value;
    }

    private void setValue(boolean a) {
        if (a) {
            mValue1 = 2;
        }
    }

    public static void setValue() {
        mValue1 = 3;
    }
    
    public void setSelf(int a) {
    	mValue1 = a;
    }
}
