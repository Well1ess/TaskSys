package nim.shs1330.netease.com.tasksys.dynamic_hook.classloader;

/**
 * Created by shs1330 on 2017/10/18.
 */

import android.content.pm.ApplicationInfo;
import android.util.ArrayMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 管理两种加载模式
 *
 *
 */
public class ClassLoaderHelper {
    public static final String ActivityThreadClz = "android.app.ActivityThread";
    public static final String ApplicationInfoClz = "android.content.pm.ApplicationInfo";
    public static final String CompatibilityInfoClz = "android.content.res.CompatibilityInfo";

    public void Method1() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Class activityThreadClz = Class.forName(ActivityThreadClz);
        Method currentActivityThreadM = activityThreadClz.getDeclaredMethod("currentActivityThread");
        currentActivityThreadM.setAccessible(true);
        Object activiyThread = currentActivityThreadM.invoke(null);

        Field mPackagesF = activityThreadClz.getDeclaredField("mPackages");
        mPackagesF.setAccessible(true);
        ArrayMap mPackages = (ArrayMap) mPackagesF.get(activiyThread);

        Method getPackageInfoNoCheck = activityThreadClz.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class, Class.forName(CompatibilityInfoClz));


    }
}
