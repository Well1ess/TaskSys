package nim.shs1330.netease.com.tasksys.binder;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import nim.shs1330.netease.com.tasksys.helper.Client;
import nim.shs1330.netease.com.tasksys.helper.LogThreadNameUtil;

/**
 * Created by shs1330 on 2017/10/9.
 */

public abstract class ComponentNative extends Binder implements Component {
    private static final String NAME = "nim.shs1330.netease.com.tasksys.binder.Component";
    private static final int TRANSACTION_add = (FIRST_CALL_TRANSACTION + 0);

    public ComponentNative() {
        this.attachInterface(this, NAME);
    }

    public static Component asInterface(IBinder obj) {
        if (obj == null) {
            Client.getHelper(LogThreadNameUtil.class)
                    .printThreadName();
            return null;
        }
        IInterface iin = obj.queryLocalInterface(NAME);
        if (iin != null && iin instanceof Component) {
            return (Component) iin;
        } else {
            return new ComponentProxy(obj);
        }
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(NAME);
                return true;
            }
            case TRANSACTION_add: {
                data.enforceInterface(NAME);
                int arg0 = data.readInt();
                this.printProcessName(arg0);
                reply.writeNoException();
                return true;

            }
        }
        return super.onTransact(code, data, reply, flags);
    }

    public static class ComponentProxy implements Component {
        private IBinder mRemote;

        public ComponentProxy(IBinder mRemote) {
            this.mRemote = mRemote;
        }

        @Override
        public void printProcessName(int a) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(NAME);
                data.writeInt(a);
                mRemote.transact(TRANSACTION_add, data, reply, 0);
                reply.readException();
            } finally {
                reply.recycle();
                data.recycle();
            }
        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }
    }
}
