package nim.shs1330.netease.com.tasksys.binder;

import android.os.Process;
import android.os.RemoteException;

/**
 * Created by shs1330 on 2017/10/10.
 */

public class ComponentService extends ComponentNative {
    @Override
    public void printProcessName(int a) throws RemoteException {
        System.out.println(a + ":" + Process.myPid());
    }
}
