package nim.shs1330.netease.com.tasksys.poster;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by shs1330 on 2017/9/29.
 */

public class ExecuteCommand {
    Object body;
    Object params;
    Method method;

    public ExecuteCommand(Object body, Object params, Method method) {
        this.body = body;
        this.params = params;
        this.method = method;
    }

    public void exeSu() {
        try {
            method.invoke(body, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}