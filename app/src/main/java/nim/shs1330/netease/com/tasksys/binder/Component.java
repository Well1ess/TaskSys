package nim.shs1330.netease.com.tasksys.binder;

import android.os.IInterface;
import android.os.RemoteException;

/**
 * Created by shs1330 on 2017/10/9.
 */

public interface Component extends IInterface {
    void printProcessName(int a) throws RemoteException;
}
