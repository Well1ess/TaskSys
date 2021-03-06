package nim.shs1330.netease.com.tasksys;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Field;

import nim.shs1330.netease.com.tasksys.binder.Component;
import nim.shs1330.netease.com.tasksys.binder.ComponentNative;

/**
 * Created by shs1330 on 2017/10/10.
 */

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private Component component;

    public AssetManager assetManager = null;
    public Resources resources = null;

    public MainActivity() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class appC = Class.forName("nim.shs1330.netease.com.parserapkfromdex.ProxyApplication");
        Field thatR = appC.getDeclaredField("thatR");
        resources = (Resources) thatR.get(null);
        assetManager = resources.getAssets();
    }


    @Override
    public AssetManager getAssets() {
        return assetManager == null ? super.getAssets() : assetManager;
    }

    @Override
    public Resources getResources() {
        return resources == null ? super.getResources() : resources;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate:  src MainActivity created");
        Log.d(TAG, "2130968603 onCreate: " + R.layout.activity_main);
//        bindService(new Intent(this, MainService.class), serviceConnection, BIND_AUTO_CREATE);
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    component.printProcessName(1000);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 2000);
//
//        findViewById(R.id.bt_showTask).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //startActivity(new Intent(MainActivity.this, TargetActivity.class));
//                try {
//                    Class helloC = ClassLoaderHelper.sDexClassloader.loadClass("Hello");
//                    Method myPrintM = helloC.getDeclaredMethod("MyPrint", String.class);
//                    myPrintM.invoke(null, "zhang !!");
//
//                    Method mainM = helloC.getDeclaredMethod("main", String[].class);
//                    mainM.invoke(null, new Object[]{ new String[]{"String"}});
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        findViewById(R.id.bt_clear).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("nim.shs1330.netease.com.plugintwp",
//                        "nim.shs1330.netease.com.plugintwp.MainActivity"));
//                startActivity(intent);
//            }
//        });
//
//        findViewById(R.id.bt_task1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                Fragment fragment = null;
//                StubFragment stubFragment = null;
//                try {
//                    ClassLoader classLoader = ClassLoaderHelper.getPluginClassLoader("nim.shs1330.netease.com.plugintwp");
//                    Class fragmentClazz = classLoader.loadClass("nim.shs1330.netease.com.plugintwp.MainFragment");
//                    fragment = (Fragment) fragmentClazz.newInstance();
//
//                    stubFragment = new StubFragment();
//                    stubFragment.setRemoteFragment(fragment);
//
//                    Method setProxyFragmentF = fragmentClazz.getDeclaredMethod("setProxyFragment", Fragment.class);
//                    setProxyFragmentF.setAccessible(true);
//                    setProxyFragmentF.invoke(fragment, stubFragment);
//
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//                transaction.replace(R.id.flyt_contrainer, stubFragment);
//                transaction.commit();
//            }
//        });
//
//        findViewById(R.id.bt_task2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction("plugtwo");
//                intent.putExtra("text", "ZZZ");
//                sendBroadcast(intent);
//            }
//        });
//
//        findViewById(R.id.bt_task3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("nim.shs1330.netease.com.plugintwp",
//                        "nim.shs1330.netease.com.plugintwp.PluginService"));
//                startService(intent);
//            }
//        });
//
//        thread.start();
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

    public Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    };

    public Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
        }
    }) {

    };
}
