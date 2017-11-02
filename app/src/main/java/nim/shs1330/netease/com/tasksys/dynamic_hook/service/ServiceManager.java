package nim.shs1330.netease.com.tasksys.dynamic_hook.service;

import android.app.Service;
import android.content.Intent;

import nim.shs1330.netease.com.tasksys.dynamic_hook.ams.AMSHook;
import nim.shs1330.netease.com.tasksys.dynamic_hook.classloader.ClassLoaderHelper;

/**
 * Created by shs1330 on 2017/11/2.
 */

public class ServiceManager {
    private static volatile ServiceManager instance;

    public synchronized static ServiceManager getInstance() {
        if (instance == null) {
            synchronized (ServiceManager.class) {
                instance = new ServiceManager();
            }
        }
        return instance;
    }

    public void onStart(Intent intent) {
        Intent rawIntent = intent.getParcelableExtra(AMSHook.TARGET_SERVICE);
        if (rawIntent == null) {
            return;
        }

        String packageName = rawIntent.getComponent().getPackageName();
        String className = rawIntent.getComponent().getClassName();

        ClassLoader pluginClassLoader = ClassLoaderHelper.getPluginClassLoader(packageName);
        try {
            Class targetServiceClz = pluginClassLoader.loadClass(className);
            Service targetService = (Service) targetServiceClz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
