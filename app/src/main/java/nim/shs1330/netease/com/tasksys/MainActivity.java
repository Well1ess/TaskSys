package nim.shs1330.netease.com.tasksys;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;

import nim.shs1330.netease.com.tasksys.binder.Component;
import nim.shs1330.netease.com.tasksys.binder.ComponentNative;
import nim.shs1330.netease.com.tasksys.dynamic_hook.activity.TargetActivity;

/**
 * Created by shs1330 on 2017/10/10.
 */

public class MainActivity extends Activity {
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

        findViewById(R.id.bt_showTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TargetActivity.class));
            }
        });

        findViewById(R.id.bt_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("nim.shs1330.netease.com.pluginone",
                        "nim.shs1330.netease.com.pluginone.MainActivity"));
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
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
