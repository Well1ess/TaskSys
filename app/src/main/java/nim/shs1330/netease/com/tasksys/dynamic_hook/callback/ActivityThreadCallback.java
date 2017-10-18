package nim.shs1330.netease.com.tasksys.dynamic_hook.callback;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;

import static nim.shs1330.netease.com.tasksys.dynamic_hook.ams.AMSHook.TARGET_ACTIVITY;

/**
 * Created by shs1330 on 2017/10/16.
 * <p>
 * 在AMS进行验证完成之后，通过调用AppThread的handleStartActivity启动Activity，
 * {@link Handler#dispatchMessage(Message)}方法首先掉头{@linkHandler.android.os.Handler.Callback}
 * 如果为空或者返回false在调用handleMessage，我们自己new一个Callback赋值个ActivityThread
 */
public class ActivityThreadCallback implements Handler.Callback {
    private Handler mH;

    public ActivityThreadCallback(Handler mH) {
        this.mH = mH;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                handleLaunchActivity(msg);
                break;
        }
        mH.handleMessage(msg);
        return true;
    }

    private void handleLaunchActivity(Message msg) {
        Object obj = msg.obj;
        try {
            Field intentF = obj.getClass().getDeclaredField("intent");
            intentF.setAccessible(true);
            Intent raw = (Intent) intentF.get(obj);
            //*******************************************************************
            Intent target = raw.getParcelableExtra(TARGET_ACTIVITY);
            if (target == null)
                return;
            raw.setComponent(target.getComponent());

            Field activityInfoField = obj.getClass().getDeclaredField("activityInfo");
            activityInfoField.setAccessible(true);

            ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(obj);
            activityInfo.applicationInfo.packageName = target.getPackage() == null ?
                    target.getComponent().getPackageName() : target.getPackage();

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
