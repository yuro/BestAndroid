package com.mock;

import android.app.Instrumentation;
import junit.framework.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by wall on 14-4-8.
 */
public class MockFramework {

    private static List<MockRecoverItem> mockRecoverItemList = new ArrayList<MockRecoverItem>();

    private static Instrumentation mInst;

    public static void init(Instrumentation inst) {
        mInst = inst;
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }

    public static Instrumentation getInstrumentation() {
        return mInst;
    }

    // recover all member had set.
    public static void recover(){
        // need to reversed order, because we should rollback the latest one first.
        for (int i = mockRecoverItemList.size() -1; i >= 0; i--) {
            try {
                MockRecoverItem item = mockRecoverItemList.get(i);
                item.mField.set(item.mReceiver, item.mMember);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        mockRecoverItemList.clear();
    }

    private static void addRecover(Field field, Object receiver, Object member) {
        MockRecoverItem item = new MockRecoverItem();
        item.mField = field;
        item.mMember = member;
        item.mReceiver = receiver;
        mockRecoverItemList.add(item);
    }

    public static void setMemberNoRecover(Object receiver, String memberName, Object mockMember) {
        try {
            Field field = getFieldByName(receiver.getClass(), memberName);
            assertNotNull(field);
            // let it crash if null
            if (Modifier.isStatic(field.getModifiers())) {
                field.set(null, mockMember);
            } else {
                field.set(receiver, mockMember);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setMember(Object receiver, String memberName, Object mockMember) {
        try {
            Field field = getFieldByName(receiver.getClass(), memberName);
            assertNotNull("not find the member:" + memberName, field);
            // let it crash if null
            if (Modifier.isStatic(field.getModifiers())) {
                addRecover(field, null, field.get(null));
                field.set(null, mockMember);
            } else {
                addRecover(field, receiver, field.get(receiver));
                field.set(receiver, mockMember);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setMember(Class<?> cls, String memberName, Object mockMember) {
        try {
            Field field = getFieldByName(cls, memberName);
            assertNotNull("not find the member:" + memberName, field);
            // let it crash if null
            if (Modifier.isStatic(field.getModifiers())) {
                addRecover(field, null, field.get(null));
                field.set(null, mockMember);
            } else {
                Assert.fail("must be a static member");
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getMember(Class<?> cls, String memberName) {
        try {
            Field field = getFieldByName(cls, memberName);
            assertNotNull("not find the member:" + memberName, field);
            // let it crash if null
            if (Modifier.isStatic(field.getModifiers())) {
                return field.get(null);
            } else {
                Assert.fail("must be a static member");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getMember(Object receiver, String memberName) {
        try {
            Field field = getFieldByName(receiver.getClass(), memberName);
            assertNotNull("not find the member:" + memberName, field);
            // let it crash if null
            if (Modifier.isStatic(field.getModifiers())) {
                return field.get(null);
            } else {
                return field.get(receiver);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(Class<?> type, String methodName, Class... args){
        Method method = null;
        try {
            method = type.getDeclaredMethod(methodName, args);
        } catch (NoSuchMethodException e) {
        }
        if (method == null) {
            // 递归父类
            Class<?> superclass = type.getSuperclass();
            if (superclass != null) {
                method = getMethod(superclass, methodName, args);
            }
        }
        //private access, let it crash to know method not found.
        assertNotNull(method);
        method.setAccessible(true);
        return method;
    }

    private static Field getFieldByName(Class<?> type, String memberName) {
        Field field = null;
        try {
            field = type.getDeclaredField(memberName);
        } catch (NoSuchFieldException e) {
        }

        if (field == null) {
            // 递归父类
            Class<?> superclass = type.getSuperclass();
            if (superclass != null) {
                field = getFieldByName(superclass, memberName);
            }
        }

        //private access, let it crash to know method not found.
        assertNotNull(field);
        field.setAccessible(true);
        return field;
    }
}

class MockRecoverItem {
    public Object mReceiver;
    public Object mMember;
    public Field mField;
}
