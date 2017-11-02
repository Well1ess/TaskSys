package nim.shs1330.netease.com.tasksys.dynamic_hook.ams;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import nim.shs1330.netease.com.tasksys.dynamic_hook.service.StubService;
import nim.shs1330.netease.com.tasksys.dynamic_hook.activity.StubActivity;
import nim.shs1330.netease.com.tasksys.helper.Client;

/**
 * Created by shs1330 on 2017/10/13.
 * <p>
 * ActivityManagerService是个系统服务，底层使用是
 * {@linkandroid.app.ActivityManagerNative#getDefault 单例}
 * AMS主要用于管理四大组件的生命周期
 * AMS在framework的system_server进程中管理所有“活动”，这样开发人员更方便的使用四大组件
 * 而AppThread就是App process和system_server进程通信的桥梁
 * <p>
 * 第一步：
 * app进程
 * {@link android.app.Activity#startActivity(Intent)}
 * {@link android.content.Context#startActivity(Intent)}
 * 两个方法通过{@link android.app.Instrumentation} IPC 调用AMS
 * <p>
 * 第二步：
 * server_system进程
 * 验证Activity和管理ActivityStacker IPC调用AppThread
 * <p>
 * 第三步：
 * app进程
 * Appthread中调用Handler转至ActivityThread的performLaunchActivity()
 * <p>
 * <p>
 * <p>
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
    public static final String TARGET_ACTIVITY = "TARGET_ACTIVITY";
    public static final String TARGET_SERVICE = "TARGET_SERVICE";
    private static final String TAG = "AMSHook";
    private Object mBaseAms;

    public AMSHook(Object object) {
        mBaseAms = object;
    }


    /**
     * Hook startActivity的具体思路就是在第一二部替换为Manifest.xml中声明的Activity，暂存目标Activity，交给AMS去验证
     * 在第二三部的时候替换为真身目标Activity
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("startActivity")) {
            Intent rawIntent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    rawIntent = (Intent) args[i];
                    index = i;
                    break;
                }
            }

            String packageName = "nim.shs1330.netease.com.tasksys";
            ComponentName c = new ComponentName(packageName, StubActivity.class.getCanonicalName());
            Intent newIntent = new Intent();
            newIntent.setComponent(c);
            Log.d(TAG, "invoke: " + rawIntent);
            newIntent.putExtra(TARGET_ACTIVITY, rawIntent);

            args[index] = newIntent;
        }

        if (method.getName().equals("startService")) {
            Intent rawIntent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    rawIntent = (Intent) args[i];
                    index = i;
                    break;
                }
            }

            Intent newIntent = new Intent();
            String proxyPackageName = Client.getContext().getPackageName();

            ComponentName componentName = new ComponentName(proxyPackageName, StubService.class.getName());
            newIntent.setComponent(componentName);

            newIntent.putExtra(TARGET_SERVICE, rawIntent);
            args[index] = newIntent;

        }
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
