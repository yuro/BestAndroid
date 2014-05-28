package testcase.mock;

import com.mock.MockFramework;

import junit.framework.Assert;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.InvocationTargetException;

import testcase.BaseTestCase;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by wallace zeng on 2014/5/28.
 */
public class MockFrameworkTest extends BaseTestCase {
    public void testGetInst() {
        Assert.assertNotNull(getInstrumentation());
    }

    public void testCase1() {
        TestA a = new TestA();
        MockFramework.setMember(a, "mValue1", 1);

        TestB b = spy(new TestB());
        doReturn(2).when(b).getValue(null);

        Assert.assertEquals(1, b.getValue(a));
        Assert.assertEquals(2, b.getValue(null));
    }

    public void testCase2() {
        TestC c = Mockito.mock(TestC.class);
        when(c.getValue(anyInt())).thenReturn(1);
        MockFramework.setMember(c, "mTestC", c);

        TestD d = new TestD();
        Assert.assertEquals(1, d.getValue(0));
    }

    // aspectJ
    public void testCase3() {
        TestA a = Mockito.mock(TestA.class);
        when(a.getValue()).thenReturn(1);
        // todo mock TestA constructor and return the mock obj

        TestB b = new TestB();
        Assert.assertEquals(1, b.getValueInter());
    }

    public void testCase4() {
        TestA a = new TestA();
        try {
            MockFramework.getMethod(TestA.class, "setValue", int.class).invoke(a, 1);
            Assert.assertEquals(1, a.getValue());
            MockFramework.getMethod(TestA.class, "setValue", boolean.class).invoke(a, true);
            Assert.assertEquals(2, a.getValue());
            MockFramework.getMethod(TestA.class, "setValue").invoke(a);
            Assert.assertEquals(3, a.getValue());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void testCase5() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        TestA a = new TestA();
        TestA mca = spy(a);
        MockFramework.getMethod(mca.getClass(), "setSelf", int.class).invoke(mca, 4);

        Assert.assertEquals(4, mca.getValue());
    }

    public void testCase6() {
        TestA a = new TestA();
        TestA mca = spy(a);
        a.setSelf(1);

        Assert.assertEquals(1, mca.getValue());

        mca.setSelf(2);
        Assert.assertEquals(2, a.getValue());
    }

    public void testCase7() {
        final TestA a = new TestA();
        TestA mca = spy(a);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                a.setSelf(3);
                return null;
            }
        }).when(mca).setSelf(anyInt());
        mca.setSelf(1);
        Assert.assertEquals(3, mca.mValue1);

        mca.mValue1 = 4;
        Assert.assertEquals(4, a.mValue1);
    }

    public void testCase11() {
        final TestE e = new TestE();
        e.mInt = 1;

        TestE me = spy(e);
        Assert.assertEquals(1, me.mInt);
    }

    public void testCase12() {
        final TestA e = new TestA();
        TestA me = spy(e);
        me.setSelf(1);

        verify(me, timeout(1)).setSelf(1);
    }

    public void testCase13() {
        final TestE e = new TestE();
        TestE me = spy(e);
        MockFramework.setMember(me, "mValue1", 1);
        Assert.assertEquals(1, me.getValue());
    }

    public void testSetStaticClass() {
        MockFramework.setMember(TestA.class, "mValue1", 2);
        Assert.assertEquals(2, TestA.mValue1);

        Assert.assertEquals(2, MockFramework.getMember(TestA.class, "mValue1"));
    }

    public void testCase9() {
        final TestE e = new TestE();
        TestE me = spy(e);

        e.mStr = "t";
        Assert.assertEquals("t", me.mStr);

        me.mStr = "k";
        Assert.assertEquals("k", e.mStr);

        e.mInt = 1;
        Assert.assertEquals(0, me.mInt);
    }
}
