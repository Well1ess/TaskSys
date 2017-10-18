package nim.shs1330.netease.com.tasksys.dynamic_hook.classloader;

/**
 * Created by shs1330 on 2017/10/18.
 */

import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import nim.shs1330.netease.com.tasksys.helper.FileHelper;

/**
 * 管理两种加载模式
 *
 *
 */
public class ClassLoaderHelper {
    private static final String TAG = "ClassLoaderHelper";
    private static final String ActivityThreadClz = "android.app.ActivityThread";
    private static final String CompatibilityInfoClz = "android.content.res.CompatibilityInfo";

    private static Map<String, Object> sLoadedApk = new HashMap<>();

    public static void hookCustomClassLoader(File apkFile) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        Class activityThreadClz = Class.forName(ActivityThreadClz);
        Method currentActivityThreadM = activityThreadClz.getDeclaredMethod("currentActivityThread");
        currentActivityThreadM.setAccessible(true);
        Object activityThread = currentActivityThreadM.invoke(null);

        Field mPackagesF = activityThreadClz.getDeclaredField("mPackages");
        mPackagesF.setAccessible(true);
        Map mPackages = (Map) mPackagesF.get(activityThread);

        Method getPackageInfoNoCheck = activityThreadClz.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class, Class.forName(CompatibilityInfoClz));
        getPackageInfoNoCheck.setAccessible(true);


        ApplicationInfo applicationInfo = generateApplication(apkFile);

        Object loadedApk = getPackageInfoNoCheck.invoke(activityThread, applicationInfo,generateCompatibilityInfo());

        CustomClassLoader customClassLoader = new CustomClassLoader(apkFile.getPath(),
                FileHelper.getOptDir(applicationInfo.packageName).getPath(),
                FileHelper.getPluginLibDir(applicationInfo.packageName).getPath(),
                ClassLoader.getSystemClassLoader());

        Field mClassLoaderFieldF = loadedApk.getClass().getDeclaredField("mClassLoader");
        mClassLoaderFieldF.setAccessible(true);
        mClassLoaderFieldF.set(loadedApk, customClassLoader);

        sLoadedApk.put(applicationInfo.packageName, loadedApk);
        mPackages.put(applicationInfo.packageName, new WeakReference(loadedApk));

        Log.d(TAG, "plugin: " + applicationInfo.packageName);
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
