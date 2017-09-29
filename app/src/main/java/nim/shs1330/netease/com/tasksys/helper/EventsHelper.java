package nim.shs1330.netease.com.tasksys.helper;

import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import nim.shs1330.netease.com.tasksys.annotation.Subscribe;
import nim.shs1330.netease.com.tasksys.annotation.ThreadMode;
import nim.shs1330.netease.com.tasksys.poster.AsyncPoster;
import nim.shs1330.netease.com.tasksys.poster.ExecuteCommand;
import nim.shs1330.netease.com.tasksys.poster.MainPoster;

/**
 * Created by shs1330 on 2017/9/28.
 */

public class EventsHelper {
    private static final String TAG = "EventsHelper";
    //初始化保存当前线程状态的Threadlocal类型的变量
    private ThreadLocal<EventHelperState> eventHelperState = new ThreadLocal<EventHelperState>() {
        @Override
        protected EventHelperState initialValue() {
            return new EventHelperState();
        }
    };
    //ParameterClass->RegistedClass
    private HashMap<Class, CopyOnWriteArraySet<Class>> registedClass = new HashMap<>();
    //RegistedClass->ParameterClass
    private HashMap<Class, CopyOnWriteArraySet<Class>> body2Params = new HashMap<>();
    //RegistedClass->Object本体
    private HashMap<Class, Object> objectBody = new HashMap<>();
    //RegistedClass->Methods Name
    private HashMap<Class, CopyOnWriteArrayList<RegistedMethodInfo>> methods = new HashMap<>();
    //线程池，固定核心线程，以便快速响应
    private Executor executor = Executors.newFixedThreadPool(5);
    //主线程的分发者
    //Looper选择主线程的，只有这样才能在主线程中执行
    private MainPoster mainPoster = new MainPoster(Looper.getMainLooper());
    //异步事件分发送者，不管当前线程状态始终在子线程中执行
    private AsyncPoster asyncPoster = new AsyncPoster(this);

    //在接受消息的类中注册
    public void register(Object o) {
        Method[] methods = o.getClass().getMethods();

        if (this.methods.containsKey(o.getClass()))
            throw new IllegalArgumentException("重复注册");

        CopyOnWriteArrayList<RegistedMethodInfo> methodList = new CopyOnWriteArrayList<>();
        CopyOnWriteArraySet<Class> paramsList = new CopyOnWriteArraySet<>();
        for (Method method : methods) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe != null) {
                if (method.getParameterTypes().length != 1) {
                    throw new IllegalArgumentException("方法必须只能有一个参数");
                }
                methodList.add(new RegistedMethodInfo(method, subscribe.threadMode(), method.getParameterTypes()[0].getName()));
                paramsList.add(method.getParameterTypes()[0]);
            }
        }

        //此类中有注册的方法
        if (methodList.size() != 0) {
            this.methods.put(o.getClass(), methodList);
            this.objectBody.put(o.getClass(), o);
            this.body2Params.put(o.getClass(), paramsList);
            for (RegistedMethodInfo info : methodList) {
                Method m = info.getReigstedMethod();
                CopyOnWriteArraySet<Class> registeds = this.registedClass.get(m.getParameterTypes()[0]);
                if (registeds == null) {
                    registeds = new CopyOnWriteArraySet<>();
                }
                registeds.add(o.getClass());
                this.registedClass.put(m.getParameterTypes()[0], registeds);
            }
        }

        Log.d(TAG, "register: " + registedClass.toString());
        Log.d(TAG, "register: " + objectBody.toString());
        Log.d(TAG, "register: " + this.methods.toString());
    }

    public void unregister(Object object) {
        Class bodyClz = object.getClass();
        if (!this.methods.containsKey(bodyClz)) {
            return;
        }

        this.methods.remove(bodyClz);
        this.objectBody.remove(bodyClz);
        CopyOnWriteArraySet<Class> params = this.body2Params.remove(bodyClz);
        for (Class paramsClz : params) {
            CopyOnWriteArraySet<Class> bodyClzs = getRegisterClassList(paramsClz);
            bodyClzs.remove(bodyClz);
            if (bodyClzs.size() == 0) {
                this.registedClass.remove(paramsClz);
            }
        }

        Log.d(TAG, "register: " + registedClass.toString());
        Log.d(TAG, "register: " + objectBody.toString());
        Log.d(TAG, "register: " + this.methods.toString());
    }

    /**
     * 发送消息
     *
     * @param event
     */
    public void post(Object event) {
        EventHelperState state = getEventHelperState();
        //事件队列
        List<Object> eventList = state.events;
        //获取当前线程的信息
        state.isMainThread = isMainThread();

        eventList.add(event);

        while (eventList.size() != 0) {
            //处理所有的消息
            handleEventInfo(eventList.remove(0));
        }
    }

    /**
     * 处理事件的信息
     *
     * @param event
     */
    private void handleEventInfo(Object event) {
        Class paramsClz = event.getClass();
        //获取注册的类
        CopyOnWriteArraySet<Class> bodyClzs = getRegisterClassList(paramsClz);
        //获取类的实体和对应注册的方法
        for (Class clazz : bodyClzs) {
            Object body = getRegisterBody(clazz);
            CopyOnWriteArrayList<EventsHelper.RegistedMethodInfo> methods = getRegisterMethod(clazz);
            for (EventsHelper.RegistedMethodInfo info : methods) {
                if (info.getParamsClzName().equals(paramsClz.getName())) {
                    dispatcher(body, info.getReigstedMethod(), event, info.threadMode);
                }
            }
        }
    }

    /**
     * 分发事件
     *
     * @param body
     * @param method
     * @param event
     * @param threadMode
     */
    private void dispatcher(Object body, Method method, Object event, ThreadMode threadMode) {
        switch (threadMode) {
            case POTING:
                //在当前线程直接调用
                new ExecuteCommand(body, event, method).exeSu();
                break;
            case MAIN:
                //主线程调用
                ExecuteCommand executeCommand = new ExecuteCommand(body, event, method);
                if (getEventHelperState().isMainThread) {
                    executeCommand.exeSu();
                } else {
                    mainPoster.sendMessage(executeCommand);
                }
                break;
            case BACKGTOUND:
                //当前线程为主线则子线程调用，当前线程为子线程直接调用
                ExecuteCommand asyncCommand = new ExecuteCommand(body, event, method);
                if (getEventHelperState().isMainThread) {
                    asyncPoster.sendMessage(asyncCommand);
                } else {
                    asyncCommand.exeSu();
                }
                break;
            case ASYNC:
                //子线程调用
                asyncPoster.sendMessage(new ExecuteCommand(body, event, method));
                break;
        }
    }

    /**
     * 判断当前线程是否是主线程
     *
     * @return
     */
    private boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    /**
     * 获取已经注册了这个参数类型的类实体列表
     *
     * @param paramsClass
     * @return
     */
    public CopyOnWriteArraySet<Class> getRegisterClassList(Class paramsClass) {
        return registedClass.get(paramsClass);
    }

    /**
     * 获取实体
     *
     * @param bodyClass
     * @return
     */
    public Object getRegisterBody(Class bodyClass) {
        return objectBody.get(bodyClass);
    }

    /**
     * 获取这个类注册了那些方法
     *
     * @param bodyClass
     * @return
     */
    public CopyOnWriteArrayList<RegistedMethodInfo> getRegisterMethod(Class bodyClass) {
        return methods.get(bodyClass);
    }

    /**
     * 由于ThreadLocal的特性，获取的是对用线程的状态
     *
     * @return
     */
    public EventHelperState getEventHelperState() {
        return eventHelperState.get();
    }

    /**
     * 线程池
     *
     * @return
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     * 保存每个线程的状态
     */
    private class EventHelperState {
        //事件
        List<Object> events = new ArrayList<>();
        //当前线程是否为主线程
        boolean isMainThread;
    }

    /**
     * 方法的封装类
     */
    public class RegistedMethodInfo {
        //方法对象
        private Method reigstedMethod;
        //注册状态
        private ThreadMode threadMode;
        //此方法的参数的名字
        private String paramsClzName;

        public RegistedMethodInfo(Method reigstedMethod, ThreadMode threadMode, String paramsClzName) {
            this.reigstedMethod = reigstedMethod;
            this.threadMode = threadMode;
            this.paramsClzName = paramsClzName;
        }

        public String getParamsClzName() {
            return paramsClzName;
        }

        public void setParamsClzName(String paramsClzName) {
            this.paramsClzName = paramsClzName;
        }

        public Method getReigstedMethod() {
            return reigstedMethod;
        }

        public void setReigstedMethod(Method reigstedMethod) {
            this.reigstedMethod = reigstedMethod;
        }

        public ThreadMode getThreadMode() {
            return threadMode;
        }

        public void setThreadMode(ThreadMode threadMode) {
            this.threadMode = threadMode;
        }

        @Override
        public String toString() {
            return "RegistedMethodInfo{" +
                    "reigstedMethod=" + reigstedMethod +
                    ", threadMode=" + threadMode +
                    ", paramsClzName='" + paramsClzName + '\'' +
                    '}';
        }
    }
}
