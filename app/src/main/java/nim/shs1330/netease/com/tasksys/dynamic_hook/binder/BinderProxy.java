package nim.shs1330.netease.com.tasksys.dynamic_hook.binder;

import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by shs1330 on 2017/10/13.
 */

public class BinderProxy implements InvocationHandler {

    private String serviceInterface = "";
    private String serviceStub = "";
    private IBinder base;

    public BinderProxy(IBinder base) {
        this.base = base;

        serviceInterface = "android.content.IClipboard";
        serviceStub = "android.content.IClipboard$Stub";
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("queryLocalInterface".equals(method.getName())) {
            return Proxy.newProxyInstance(proxy.getClass().getClassLoader(),
                    new Class[]{IBinder.class, IInterface.class, Class.forName(serviceInterface)},
                    new ClipBoardServiceHook(base, Class.forName(serviceStub)));
        }
        return null;
    }
}
