package nim.shs1330.netease.com.tasksys.dynamic_hook.classloader;

import dalvik.system.DexClassLoader;

/**
 * Created by 张丽华 on 2017/10/16.
 * Description:
 * ClassLoader机制
 * 具体思路ActivityThread中的performLaunchActivity方法通过反射new一个Activity，
 * 往上一层是handleLaunchActivity new一个ActivityInfoRecord，AIR.appInfo = LoadedApk,这个AIR里面有个LoadedApk，就是它里面的classLoader
 * 加载Activity的类。
 * 每一个不同的包对应一个LoadedApk，里面有一个Map有PackageName和LoadedApk的对应关系。
 * 我们可以new自己的classLoader加入对应的Map。
 * ActivityInfoRecord中r.activityInfo.getClassLoader();
 */

public class CustomClassLoader extends DexClassLoader {
    public CustomClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }
}
