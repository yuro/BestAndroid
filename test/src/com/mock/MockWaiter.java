package com.mock;

import java.util.concurrent.TimeUnit;

public class MockWaiter {
    private static final int MINTIMEOUT = 100; //100ms
    // to return the result
    private MockResult mMockResult = MockResult.Working;
    // to finish waiting
    private boolean mStick = false;
    // to callback stop mock
    private Runnable mCallback;
    // to show error log
    private String mLog;

    public MockWaiter() {
        mCallback = null;
    }

    public MockWaiter(Runnable finalCallBack) {
        mCallback = finalCallBack;
    }

    public void finish() {
        mStick = true;
    }

    public void setResult(MockResult result) {
        // to advoid after timeout, the handler still want to change the result;
        if (mMockResult == MockResult.TimeOut) return;
        mMockResult = result;
    }

    public void verify(String log, boolean ret) {
        // to avoid multi error log
        if (mStick) return;
        setResult(ret ? MockResult.Success : MockResult.Fail);
        if (!ret) {
            mLog = log;
            finish();
        }
    }

    public String getLog() {
        return mLog;
    }

    public MockResult getResult() {
        return mMockResult;
    }

    public void setResult(Boolean result) {
        setResult(result ? MockResult.Success : MockResult.Fail);
    }

    public MockResult waitResult(int second) {
        try {
            // here cannot used object.wait(), because we will changed the system time and it will timeout.
            int times = second * 10;
            for (int i = 0; i < times; i++) {
                TimeUnit.MILLISECONDS.sleep(MINTIMEOUT);
                if (mStick) {
                    break;
                }

                if (i == times - 1) {
                    setResult(MockResult.TimeOut);
                }
            }
        } catch (InterruptedException e) {
            setResult(MockResult.Fail);
            e.printStackTrace();
        } finally {
            if (mCallback != null) {
                mCallback.run();
            }
        }
        return mMockResult;
    }
}
