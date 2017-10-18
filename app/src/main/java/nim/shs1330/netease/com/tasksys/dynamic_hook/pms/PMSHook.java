package nim.shs1330.netease.com.tasksys.dynamic_hook.pms;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by shs1330 on 2017/10/13.
 * <p>
 * PMS负责权限验证等
 * 我们在四大组件中调用getPackageManager()方法时，真正的方法在{@link Context#getPackageManager()}中
 * 而{@link Context}的具体实现是{@linkContextImpl}在其中可以看到访问的是{@linkActivityThread.getPackageManager}
 * 的方法
 * IPackageManager
 * PackageManager.Stub
 * PackageManagerService{@linkPackageManagerService}
 */

public class PMSHook implements InvocationHandler {
    private static final String TAG = "PMSHook";
    private Object mBasePms;

    public PMSHook(Object mBasePms) {
        this.mBasePms = mBasePms;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "invoke: " + method.getName());
        if (method.getName().equals("getActivityInfo")){
            ActivityInfo activityInfo = new ActivityInfo();
            return activityInfo;
        }
        if (method.getName().equals("getPackageInfo")) {
            return new PackageInfo();
        }
        return method.invoke(mBasePms, args);
    }

    public static void pmsHook(){
        try {
            Class activityThread = Class.forName("android.app.ActivityThread");
            Field sPackageManagerF = activityThread.getDeclaredField("sPackageManager");
            sPackageManagerF.setAccessible(true);
            Object sPackageManager = sPackageManagerF.get(null);

            Object pmsHook = Proxy.newProxyInstance(activityThread.getClassLoader(),
                    new Class[]{Class.forName("android.content.pm.IPackageManager")},
                    new PMSHook(sPackageManager));
            sPackageManagerF.set(null, pmsHook);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
