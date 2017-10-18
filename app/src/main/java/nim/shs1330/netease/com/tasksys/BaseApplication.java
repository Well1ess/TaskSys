package nim.shs1330.netease.com.tasksys;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;

import nim.shs1330.netease.com.tasksys.dynamic_hook.Hook;
import nim.shs1330.netease.com.tasksys.dynamic_hook.classloader.ClassLoaderHelper;
import nim.shs1330.netease.com.tasksys.helper.Client;
import nim.shs1330.netease.com.tasksys.helper.FileHelper;

/**
 * Created by shs1330 on 2017/10/11.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Client.init(getApplicationContext());

        FileHelper.extractAssets("app-debug.apk");
        try {
            ClassLoaderHelper.hookCustomClassLoader(getFileStreamPath("app-debug.apk"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //hookInstrumentation
        Client.getHelper(Hook.class).hook();
        //hook ClipBoard IBinder
        Client.getHelper(Hook.class).hookBinder();
        //hook ActivityManagerService
        Client.getHelper(Hook.class).hookAMS();
        //hook PackageManagerService
        Client.getHelper(Hook.class).hookPMS();
        //hook ActivityThread H mCallback
        Client.getHelper(Hook.class).hookHandler();
    }
}
