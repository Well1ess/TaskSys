package nim.shs1330.netease.com.tasksys.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nim.shs1330.netease.com.tasksys.MainActivity;

/**
 * Created by shs1330 on 2017/10/12.
 */

public class LaunchActivity extends BroadcastReceiver {
    public static final String InvokeActivity = "InvokeActivity";
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
