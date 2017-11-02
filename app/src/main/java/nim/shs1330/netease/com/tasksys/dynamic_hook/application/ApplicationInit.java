package nim.shs1330.netease.com.tasksys.dynamic_hook.application;

import android.app.Application;
import android.app.Instrumentation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import nim.shs1330.netease.com.tasksys.dynamic_hook.classloader.ClassLoaderHelper;

/**
 * Created by shs1330 on 2017/10/30.
 */

public class ApplicationInit {
    private static final String TAG = "ApplicationInit";
    private static Map<String, Application> sApplication = new HashMap<>();

    public static Application getPluginApplication(String packageName){
        return sApplication.get(packageName);
    }

    public static void CreatePluginApplication(String packageName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {

        Object loadedApk = ClassLoaderHelper.getLoadedApk(packageName);
        Class<?> activityThreadClz = Class.forName("android.app.ActivityThread");
        Method getCurrentActivityThread = activityThreadClz.getDeclaredMethod("currentActivityThread");
        getCurrentActivityThread.setAccessible(true);
        Object activityThread = getCurrentActivityThread.invoke(null);

        Field mInstrumentation = activityThreadClz.getDeclaredField("mInstrumentation");
        mInstrumentation.setAccessible(true);
        Instrumentation instrumentation = (Instrumentation) mInstrumentation.get(activityThread);

        Class loadedApkClz = Class.forName("android.app.LoadedApk");
        Method makeApplicationM = loadedApkClz.getDeclaredMethod("makeApplication", boolean.class,
                Instrumentation.class);
        makeApplicationM.setAccessible(true);
        Application application = (Application) makeApplicationM.invoke(loadedApk, false, instrumentation);

        sApplication.put(packageName, application);
    }
}
