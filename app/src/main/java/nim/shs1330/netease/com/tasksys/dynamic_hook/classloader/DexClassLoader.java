package nim.shs1330.netease.com.tasksys.dynamic_hook.classloader;

/**
 * Created by shs1330 on 2017/12/26.
 */

public class DexClassLoader extends dalvik.system.DexClassLoader {
    public DexClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }
}
