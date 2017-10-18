package nim.shs1330.netease.com.tasksys.dynamic_hook.callback;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import nim.shs1330.netease.com.tasksys.dynamic_hook.pms.PMSHook;

import static nim.shs1330.netease.com.tasksys.dynamic_hook.ams.AMSHook.TARGET_ACTIVITY;

/**
 * Created by shs1330 on 2017/10/16.
 */

/**
 * 在AMS进行验证完成之后，通过调用AppThread的handleStartActivity启动Activity，
 * {@link Handler#dispatchMessage(Message)}方法首先掉头{@linkHandler.android.os.Handler.Callback}
 * 如果为空或者返回false在调用handleMessage，我们自己new一个Callback赋值个ActivityThread
 */
public class ActivityThreadCallback implements Handler.Callback {
    private Handler mH;

    public ActivityThreadCallback(Handler mH) {
        this.mH = mH;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                handleLaunchActivity(msg);
                break;
        }
        mH.handleMessage(msg);
        return true;
    }

    private void handleLaunchActivity(Message msg) {
        Object obj = msg.obj;
        try {
            Field intentF = obj.getClass().getDeclaredField("intent");
            intentF.setAccessible(true);
            Intent raw = (Intent) intentF.get(obj);
            //*******************************************************************
            Intent target = raw.getParcelableExtra(TARGET_ACTIVITY);
            if (target == null)
                return;
            raw.setComponent(target.getComponent());

            Field activityInfoField = obj.getClass().getDeclaredField("activityInfo");
            activityInfoField.setAccessible(true);

            ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(obj);
            activityInfo.applicationInfo.packageName = target.getPackage() == null ?
                    target.getComponent().getPackageName() : target.getPackage();

            hookPackageManager();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void hookPackageManager() throws Exception {

        // 这一步是因为 initializeJavaContextClassLoader 这个方法内部无意中检查了这个包是否在系统安装
        // 如果没有安装, 直接抛出异常, 这里需要临时Hook掉 PMS, 绕过这个检查.

        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);

        // 获取ActivityThread里面原始的 sPackageManager
        Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
        sPackageManagerField.setAccessible(true);
        Object sPackageManager = sPackageManagerField.get(currentActivityThread);

        // 准备好代理对象, 用来替换原始的对象
        Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
        Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                new Class<?>[] { iPackageManagerInterface },
                new PMSHook(sPackageManager));

        // 1. 替换掉ActivityThread里面的 sPackageManager 字段
        sPackageManagerField.set(currentActivityThread, proxy);
    }
}
