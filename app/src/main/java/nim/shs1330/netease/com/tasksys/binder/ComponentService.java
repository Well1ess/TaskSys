package nim.shs1330.netease.com.tasksys.binder;

import android.os.RemoteException;

/**
 * Created by shs1330 on 2017/10/10.
 */

public class ComponentService extends ComponentNative {
    @Override
    public void printProcessName(int a) throws RemoteException {
        System.out.println("调用的是Service的本地方法，远程方法调用，上一个方法是onTransact！");
    }
}
