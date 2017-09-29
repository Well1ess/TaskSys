package nim.shs1330.netease.com.tasksys.helper;

import java.util.HashMap;

/**
 * Created by shs1330 on 2017/9/28.
 */

public class Client {
    //功能类单例的管理类
    private static HashMap<Class<?>, Object> helpers = new HashMap<>();
    public static <T> T getHelper(Class<T> tClass){
        if (helpers == null){
            helpers = new HashMap<>();
        }

        T target = (T) helpers.get(tClass);
        if (target == null){
            try {
                target = tClass.newInstance();
                helpers.put(tClass, target);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return target;
    }
}
