package testcase;

import java.util.concurrent.TimeUnit;

import android.test.ActivityInstrumentationTestCase2;

import com.example.app.FullscreenActivity;
import com.mock.MockFramework;

/**
 * Created by wallace on 14-3-26.
 */
public class BaseTestCase extends ActivityInstrumentationTestCase2<FullscreenActivity> {
    private static int rIndex = 0;
    private static int rMinUCStartupTime = 2;
    private static boolean sIsFirstRun = false;

    public BaseTestCase() {
        super(FullscreenActivity.class);
        if (!sIsFirstRun) {
            sIsFirstRun = true;
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (rIndex == 0) {
            // to wait uc startup
            try {
                MockFramework.init(getInstrumentation());
                getActivity();
                TimeUnit.SECONDS.sleep(rMinUCStartupTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // to clear all mock set member
        MockFramework.recover();
    }

    @Override
    protected void runTest() throws Throwable {
        rIndex++;
        try {
            super.runTest();
        } catch (Throwable e) {
            throw e;
        }
    }
}
