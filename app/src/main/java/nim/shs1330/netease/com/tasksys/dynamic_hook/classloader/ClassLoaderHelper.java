package nim.shs1330.netease.com.tasksys.dynamic_hook.classloader;

/**
 * Created by shs1330 on 2017/10/18.
 */

import android.content.pm.ApplicationInfo;
import android.util.ArrayMap;

import java.io.File;
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
    public static final String CompatibilityInfoClz = "android.content.res.CompatibilityInfo";

    public void Method1(File apkFile) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        Class activityThreadClz = Class.forName(ActivityThreadClz);
        Method currentActivityThreadM = activityThreadClz.getDeclaredMethod("currentActivityThread");
        currentActivityThreadM.setAccessible(true);
        Object activityThread = currentActivityThreadM.invoke(null);

        Field mPackagesF = activityThreadClz.getDeclaredField("mPackages");
        mPackagesF.setAccessible(true);
        ArrayMap mPackages = (ArrayMap) mPackagesF.get(activityThread);

        Method getPackageInfoNoCheck = activityThreadClz.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class, Class.forName(CompatibilityInfoClz));
        getPackageInfoNoCheck.setAccessible(true);

        Object loadedApk = getPackageInfoNoCheck.invoke(activityThread, generateApplication(apkFile),generateCompatibilityInfo());


    }

    private  static ApplicationInfo generateApplication(File apkFile) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class packageParserClass = Class.forName("android.content.pm.PackageParser");

        Class packageParser$PackageClass = Class.forName("android.content.pm.PackageParser$Package");
        Class packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
        Method generateApplicationInfo = packageParserClass.getDeclaredMethod("generateApplicationInfo", packageParser$PackageClass,
                int.class, packageUserStateClass);
        generateApplicationInfo.setAccessible(true);
        Object parser = packageParserClass.newInstance();
        Method parsePackage = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
        parsePackage.setAccessible(true);

        Object packageObject = parsePackage.invoke(parser, apkFile, 0);

        ApplicationInfo applicationInfo = (ApplicationInfo)generateApplicationInfo.invoke(parser, packageObject, 0, packageUserStateClass.newInstance());
        applicationInfo.sourceDir = apkFile.getPath();
        applicationInfo.publicSourceDir = apkFile.getPath();
        return  applicationInfo;
    }

    private static Object generateCompatibilityInfo() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class CompatibilityInfoClass = Class.forName(CompatibilityInfoClz);
        Field defaultCompatibilityInfoField = CompatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
        defaultCompatibilityInfoField.setAccessible(true);
        return defaultCompatibilityInfoField.get(null);
    }
}
