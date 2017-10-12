package nim.shs1330.netease.com.tasksys.dynamic_hook.instrument;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
}
