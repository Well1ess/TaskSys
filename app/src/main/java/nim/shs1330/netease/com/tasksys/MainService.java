package nim.shs1330.netease.com.tasksys;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import nim.shs1330.netease.com.tasksys.binder.ComponentService;

public class MainService extends Service {
    private ComponentService componentService;

    public MainService() {
        componentService = new ComponentService();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return componentService;
    }
}
