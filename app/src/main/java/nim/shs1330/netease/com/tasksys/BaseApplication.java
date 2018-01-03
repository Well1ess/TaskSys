package nim.shs1330.netease.com.tasksys;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import nim.shs1330.netease.com.tasksys.dynamic_hook.Hook;
import nim.shs1330.netease.com.tasksys.dynamic_hook.classloader.ClassLoaderHelper;
import nim.shs1330.netease.com.tasksys.dynamic_hook.json.JSONParser;
import nim.shs1330.netease.com.tasksys.helper.Client;
import nim.shs1330.netease.com.tasksys.helper.FileHelper;

/**
 * Created by shs1330 on 2017/10/11.
 */

public class BaseApplication extends Application {
    private static final String PluginOne = "app-debug.apk";
    private static final String DexFile = "Hello.dex";
    private static final String TAG = "BaseApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        Client.init(getApplicationContext());

        FileHelper.extractAssets(PluginOne);
        FileHelper.extractAssets(DexFile);
//        File dexFile = getFileStreamPath(PluginOne);
//        File optDexFile = getFileStreamPath("app-debug.dex");
//        ClassLoaderHelper.hookParentClassLoader(getClassLoader(), dexFile, optDexFile);

        try {
            JSONParser.parser();
        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            ClassLoaderHelper.hookCustomClassLoader(getFileStreamPath(PluginOne));
            ClassLoaderHelper.hookDexClassloader(getFileStreamPath(DexFile));
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
        //Client.init(getApplicationContext());
        Log.d(TAG, "onCreate: " + "source apk install");
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
