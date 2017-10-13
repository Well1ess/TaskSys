package nim.shs1330.netease.com.tasksys.dynamic_hook.binder;

import android.content.ClipData;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by shs1330 on 2017/10/13.
 */

public class ClipBoardServiceHook implements InvocationHandler {
    private static final String TAG = "ClipBoardServiceHook";
    private Object mBaseClip;

    public ClipBoardServiceHook(IBinder baseClip, Class<?> clz) {
        try {
            this.mBaseClip = AsInterface.createBase(baseClip, clz);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 把剪切版的内容替换为 "you are hooked"
        if ("getPrimaryClip".equals(method.getName())) {
            Log.d(TAG, "hook getPrimaryClip");
            return ClipData.newPlainText(null, "you are hooked");
        }

        // 欺骗系统,使之认为剪切版上一直有内容
        if ("hasPrimaryClip".equals(method.getName())) {
            return true;
        }

        return null;
    }
}
