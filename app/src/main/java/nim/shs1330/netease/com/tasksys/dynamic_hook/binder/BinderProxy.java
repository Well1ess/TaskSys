package nim.shs1330.netease.com.tasksys.dynamic_hook.binder;

import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by shs1330 on 2017/10/13.
 */

/**
 * 在ServiceManager的map中找到原始{@link IBinder},然后对其进行{@link InvocationHandler}的代理
 * 拦截{@link IBinder#queryLocalInterface(String)}的方法；
 * {@link IBinder#queryLocalInterface(String)}被拦截即可返回我们自己的Service
 * 自己的Service对原始的服务接口（{@link IInterface}携带的）同样也使用{@link InvocationHandler}进行代理
 * 然后我们就可以在第二代理对象中操作我们想要的方法了
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
