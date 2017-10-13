package nim.shs1330.netease.com.tasksys;

import android.app.Application;
import android.content.Context;

import nim.shs1330.netease.com.tasksys.dynamic_hook.Hook;
import nim.shs1330.netease.com.tasksys.helper.Client;

/**
 * Created by shs1330 on 2017/10/11.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Client.init(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Client.getHelper(Hook.class).hook();
        Client.getHelper(Hook.class).hookBinder();
    }
}
