package nim.shs1330.netease.com.tasksys.dynamic_hook.classloader;

/**
 * Created by shs1330 on 2017/10/18.
 */

import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import nim.shs1330.netease.com.tasksys.helper.FileHelper;

/**
 * 管理两种加载模式
 *
 *
 */
public class ClassLoaderHelper {

    private static final String TAG = "ClassLoaderHelper";
    //持有apk，防止被GC
    private static Map<String, Object> sLoadedApk = new HashMap<>();
    private static Map<String, ClassLoader> sPluginClassLoader = new HashMap<>();

    /**
     * 获取对应的Classloader
     * @param apkName
     * @return
     */
    public static ClassLoader getPluginClassLoader(String apkName)
    {
        return sPluginClassLoader.get(apkName);
    }

    public static void hookParentClassLoader(ClassLoader classLoader, File apkFile, File optDex)
    {
        //LoadedApk中mClassLoader由BaseDexClassLoader中的Element数组获取生成
        //我们通过反射构造自己的Apk对应的Element加到BaseDexClassLoader中就可以委托系统帮我
        //们生成对应的ClassLoader
        try {
            Field pathListF = DexClassLoader.class.getSuperclass().getDeclaredField("pathList");
            pathListF.setAccessible(true);
            //获取唯一的List的Object
            Object pathList = pathListF.get(classLoader);

            Field dexElementsF = pathList.getClass().getDeclaredField("dexElements");
            dexElementsF.setAccessible(true);
            //获取Element数组
            Object[] dexElements = (Object[]) dexElementsF.get(pathList);

            Class elementClass = dexElements.getClass().getComponentType();

            //新的数组
            Object[] newDexElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);
            Constructor constructor = elementClass.getConstructor(File.class, boolean.class, File.class, DexFile.class);

            //我们apk对应的element
            Object customElement = constructor.newInstance(apkFile, false, apkFile, DexFile.loadDex(apkFile.getCanonicalPath(), optDex.getAbsolutePath(), 0));

            Object[] addArray = new Object[]{customElement};

            System.arraycopy(dexElements, 0, newDexElements, 0, dexElements.length);
            System.arraycopy(addArray, 0, newDexElements, dexElements.length, addArray.length);

            //替换
            dexElementsF.set(pathList, newDexElements);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 激进的hook方法，每个Apk都有一个与之对应的ClassLoader
     * 好处是各个插件之间没有耦合，
     * 坏处是比较麻烦
     * @param apkFile
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws InstantiationException
     */
    public static void hookCustomClassLoader(File apkFile) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        //获取ActivityThread
        Class activityThreadClz = Class.forName("android.app.ActivityThread");
        //访问ActivityThread的方法
        Method currentActivityThreadM = activityThreadClz.getDeclaredMethod("currentActivityThread");
        currentActivityThreadM.setAccessible(true);
        //ActivityThread对象
        Object activityThread = currentActivityThreadM.invoke(null);

        Field mPackagesF = activityThreadClz.getDeclaredField("mPackages");
        mPackagesF.setAccessible(true);
        //ActivityThread中PackageName和LoadedApk的对应Map
        Map mPackages = (Map) mPackagesF.get(activityThread);

        //获得生成LoadedApk的方法
        Method getPackageInfoNoCheck = activityThreadClz.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class, Class.forName("android.content.res.CompatibilityInfo"));
        getPackageInfoNoCheck.setAccessible(true);

        //生成LoadedApk必须有对应apk的AndroidManifest.xml的信息
        //与之对应的是ApplicationInfo
        ApplicationInfo applicationInfo = generateApplication(apkFile);

        Object loadedApk = getPackageInfoNoCheck.invoke(activityThread, applicationInfo, generateCompatibilityInfo());
        //构造自己的ClassLoader
        CustomClassLoader customClassLoader = new CustomClassLoader(apkFile.getPath(),
                FileHelper.getOptDir(applicationInfo.packageName).getPath(),
                FileHelper.getPluginLibDir(applicationInfo.packageName).getPath(),
                ClassLoader.getSystemClassLoader());

        Field mClassLoaderFieldF = loadedApk.getClass().getDeclaredField("mClassLoader");
        mClassLoaderFieldF.setAccessible(true);
        mClassLoaderFieldF.set(loadedApk, customClassLoader);

        sPluginClassLoader.put(applicationInfo.packageName, customClassLoader);
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
        Class CompatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
        Field defaultCompatibilityInfoField = CompatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
        defaultCompatibilityInfoField.setAccessible(true);
        return defaultCompatibilityInfoField.get(null);
    }
}
