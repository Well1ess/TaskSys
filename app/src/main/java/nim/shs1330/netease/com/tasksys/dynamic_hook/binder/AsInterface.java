package nim.shs1330.netease.com.tasksys.dynamic_hook.binder;

import android.os.IBinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by shs1330 on 2017/10/13.
 */

public class AsInterface {
    public static Object createBase(IBinder base, Class<?> clz) throws NoSuchMethodException {
        Method method = clz.getMethod("asInterface", IBinder.class);
        method.setAccessible(true);
        try {
            return method.invoke(base);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
