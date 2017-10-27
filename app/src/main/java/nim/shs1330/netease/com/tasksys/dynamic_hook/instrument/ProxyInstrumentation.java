package nim.shs1330.netease.com.tasksys.dynamic_hook.instrument;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nim.shs1330.netease.com.tasksys.helper.Client;

/**
 * Created by shs1330 on 2017/10/12.
 */

public class ProxyInstrumentation extends Instrumentation {
    private static final String TAG = "ProxyInstrumentation";
    private Instrumentation mBase;

    public ProxyInstrumentation(Instrumentation instrumentation) {
        this.mBase = instrumentation;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        Log.d(TAG, "execStartActivity");
        try {
            Method proxyMethod = mBase.getClass().getMethod("execStartActivity", Context.class,
                    IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
            proxyMethod.setAccessible(true);
            return (ActivityResult) proxyMethod.invoke(mBase, who, contextThread, token, target, intent, requestCode, options);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void callApplicationOnCreate(Application app) {
        if (!app.getApplicationInfo().packageName.equals("nim.shs1330.netease.com.tasksys")){
            try {
                Method setHostContextM = app.getClass().getDeclaredMethod("setHostContext", Context.class);
                setHostContextM.setAccessible(true);
                setHostContextM.invoke(app, Client.getContext());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        super.callApplicationOnCreate(app);
    }

    public void callActivityOnCreate(Activity activity, Bundle icicle)
    {
        Log.d(TAG, "callActivityOnCreate: " + activity.getApplicationInfo().packageName);
        Log.d(TAG, "callActivityOnCreate: " + Client.getContext().getPackageName());
        if (!activity.getApplicationInfo().packageName.equals(Client.getContext().getPackageName())){
            try {
                Method setHostContextM = activity.getClass().getDeclaredMethod("setHostContext", Context.class);
                setHostContextM.setAccessible(true);
                setHostContextM.invoke(activity, Client.getContext());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        super.callActivityOnCreate(activity,icicle);
    }

    public void callActivityOnCreate(Activity activity, Bundle icicle,
                                     PersistableBundle persistentState) {
        Log.d(TAG, "callActivityOnCreate: " + activity.getApplicationInfo().packageName);
        Log.d(TAG, "callActivityOnCreate: " + getContext().getPackageName());
        if (!activity.getApplicationInfo().packageName.equals(getContext().getPackageName())){
            try {
                Method setHostContextM = activity.getClass().getDeclaredMethod("setHostContext", Context.class);
                setHostContextM.setAccessible(true);
                setHostContextM.invoke(activity, Client.getContext());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        super.callActivityOnCreate(activity, icicle, persistentState);
    }
}
