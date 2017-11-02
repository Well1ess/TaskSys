package nim.shs1330.netease.com.tasksys.dynamic_hook.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import nim.shs1330.netease.com.tasksys.dynamic_hook.ams.AMSHook;
import nim.shs1330.netease.com.tasksys.dynamic_hook.application.ApplicationInit;

/**
 * Created by shs1330 on 2017/11/2.
 */

/**
 * 在这里进行targetService的创建
 */
public class ServiceManager {
    private static final String TAG = "ServiceManager";
    private static volatile ServiceManager instance;

    private static Map<String, Service> sService = new HashMap<>();

    public synchronized static ServiceManager getInstance() {
        if (instance == null) {
            synchronized (ServiceManager.class) {
                instance = new ServiceManager();
            }
        }
        return instance;
    }

    public void onStart(Intent intent, int startId) {

        Intent rawIntent = intent.getParcelableExtra(AMSHook.TARGET_SERVICE);
        if (rawIntent == null) {
            return;
        }

        String packageName = rawIntent.getComponent().getPackageName();
        String className = rawIntent.getComponent().getClassName();

        if (!sService.containsKey(className)) {
            createService(packageName, className);
        }
        Service service = sService.get(className);
        service.onStart(intent, startId);
    }

    private void createService(String packageName, String className) {

        IBinder token = new Binder();

        try {

            //构建ServiceInfo
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.applicationInfo = ApplicationInit.getPluginApplication(packageName).getApplicationInfo();
            serviceInfo.name = className;

            //构建Compatibility
            Class compatibilityClz = Class.forName("android.content.res.CompatibilityInfo");
            Field defaultCompatibilityF = compatibilityClz.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
            Object defaultCompatibility = defaultCompatibilityF.get(null);

            Class serviceDataClz = Class.forName("android.app.ActivityThread$CreateServiceData");
            Constructor constructor = serviceDataClz.getDeclaredConstructor();
            constructor.setAccessible(true);
            //构建ServiceData
            Object serviceData = constructor.newInstance();

            //注入
            Field infoF = serviceDataClz.getDeclaredField("info");
            infoF.setAccessible(true);
            infoF.set(serviceData, serviceInfo);

            //注入
            Field compatInfoF = serviceDataClz.getDeclaredField("compatInfo");
            compatInfoF.setAccessible(true);
            compatInfoF.set(serviceData, defaultCompatibility);

            //注入
            Field tokenField = serviceDataClz.getDeclaredField("token");
            tokenField.setAccessible(true);
            tokenField.set(serviceData, token);

            //获取ActivityThread
            Class<?> activityThreadClz = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadM = activityThreadClz.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadM.invoke(null);

            //获取方法
            Method handleCreateServiceM = activityThreadClz.getDeclaredMethod("handleCreateService", serviceDataClz);
            handleCreateServiceM.setAccessible(true);

            handleCreateServiceM.invoke(currentActivityThread, serviceData);

            //获取该Service
            Field mServicesF = activityThreadClz.getDeclaredField("mServices");
            mServicesF.setAccessible(true);
            Map mServices = (Map) mServicesF.get(currentActivityThread);
            Service service = (Service) mServices.get(token);

            mServices.remove(token);

            sService.put(className, service);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
