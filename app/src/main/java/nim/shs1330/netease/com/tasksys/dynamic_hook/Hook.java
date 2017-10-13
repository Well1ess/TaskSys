package nim.shs1330.netease.com.tasksys.dynamic_hook;

import android.app.Instrumentation;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import nim.shs1330.netease.com.tasksys.dynamic_hook.binder.BinderProxy;
import nim.shs1330.netease.com.tasksys.dynamic_hook.instrument.ProxyInstrumentation;

/**
 * Created by shs1330 on 2017/10/12.
 * <p>
 * Activity的启动方式目前有Context和Activity两种，
 * Context的启动方式是调用ContexImpl的@startActivity()方法，
 * 期中底层调用的是ActivityThread的Instrumentation的execStartActivity()方法AMS->ApplicationThread
 * Activity调用的是自己的Instrumentation
 */
public class Hook {
    private static final String TAG = "Hook";
    public void hook() {
        try {
            Class<?> activityThreadClz = Class.forName("android.app.ActivityThread");
            Method getCurrentActivityThread = activityThreadClz.getDeclaredMethod("currentActivityThread");
            getCurrentActivityThread.setAccessible(true);
            Object activityThread = getCurrentActivityThread.invoke(null);

            Field mInstrumentation = activityThreadClz.getDeclaredField("mInstrumentation");
            mInstrumentation.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) mInstrumentation.get(activityThread);

            ProxyInstrumentation proxyInstrumentation = new ProxyInstrumentation(instrumentation);

            mInstrumentation.set(activityThread, proxyInstrumentation);
            Log.d(TAG, "hook完毕");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hook时应当注意，使用java反射时静态方法TargetObject为null
     * Field静态字段TargetObject为null
     * {@link Class#getDeclaredMethod(String, Class[])}，
     * {@link Class#getDeclaredField(String)}是这个类的所有字段，
     * 使用时{@link Method#setAccessible(boolean)}
     * 使用时{@link Field#setAccessible(boolean)}
     * {@link Class#getField(String)},{@link Class#getMethod(String, Class[])}返回该类的public方法
     */
    public void hookBinder(){
        try {
            Class clz = Class.forName("android.os.ServiceManager");
            try {
                Field sCache = clz.getDeclaredField("sCache");
                sCache.setAccessible(true);
                HashMap<String, IBinder> cache = (HashMap<String, IBinder>) sCache.get(null);
                IBinder rawBinder = cache.get("clipboard");
                IBinder hookedBinder = (IBinder) Proxy.newProxyInstance(clz.getClassLoader(),
                        new Class[]{IBinder.class},
                        new BinderProxy(rawBinder));
                cache.put("clipboard", hookedBinder);
                sCache.set(null, cache);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
