package nim.shs1330.netease.com.tasksys.dynamic_hook.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StubService extends Service {
    public StubService() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        ServiceManager.getInstance().onStart(intent, startId);
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
