package nim.shs1330.netease.com.tasksys.dynamic_hook.ams;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by shs1330 on 2017/10/13.
 * <p>
 * ActivityManagerService是个系统服务，底层使用是
 * {@linkandroid.app.ActivityManagerNative#getDefault 单例}
 * AMS主要用于管理四大组件的生命周期
 * <p>
 * Singleton的代码
 * public abstract class Singleton<T> {
 * private T mInstance;
 * <p>
 * protected abstract T create();
 * <p>
 * public final T get() {
 * synchronized (this) {
 * if (mInstance == null) {
 * mInstance = create();
 * }
 * return mInstance;
 * }
 * }
 * }
 */
public class AMSHook implements InvocationHandler {
    private static final String TAG = "AMSHook";
    private Object mBaseAms;

    public AMSHook(Object object) {
        mBaseAms = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, method.getName());
        return method.invoke(mBaseAms, args);
    }

    /**
     * 原理就是hookamn中的gDefault中的mInstance
     */
    public static void amsHook() {
        String amnClzName = "android.app.ActivityManagerNative";
        String iamClzName = "android.app.IActivityManager";
        try {
            Class amnClz = Class.forName(amnClzName);
            Field gDefaultF = amnClz.getDeclaredField("gDefault");
            gDefaultF.setAccessible(true);
            Object gDefault = gDefaultF.get(null);

            Class singletonClz = Class.forName("android.util.Singleton");
            Field mInstanceF = singletonClz.getDeclaredField("mInstance");
            mInstanceF.setAccessible(true);
            Object mInstance = mInstanceF.get(gDefault);

            Object amnProxy = Proxy.newProxyInstance(amnClz.getClassLoader(),
                    new Class[]{Class.forName(iamClzName)},
                    new AMSHook(mInstance));

            mInstanceF.set(gDefault, amnProxy);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}