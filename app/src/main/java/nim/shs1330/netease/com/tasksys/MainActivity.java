package nim.shs1330.netease.com.tasksys;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import nim.shs1330.netease.com.tasksys.binder.Component;
import nim.shs1330.netease.com.tasksys.binder.ComponentNative;

/**
 * Created by shs1330 on 2017/10/10.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Component component;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService(new Intent(this, MainService.class), serviceConnection, BIND_AUTO_CREATE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    component.printProcessName(1000);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            component = ComponentNative.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


}
