package nim.shs1330.netease.com.tasksys.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shs1330 on 2017/10/11.
 */

public abstract class SimpleAsyncTask<Params, Progress, Result> {
    private static final String TAG = "SimpleAsyncTask";

    public static final int TASK_COMPLETE = 0x11;
    public static final int coreNum = Runtime.getRuntime().availableProcessors();
    public static final int CORE_THREAD_NUM = Math.max(2, Math.min(coreNum - 1, 4));
    public static final int MAX_THREAD_NUM = coreNum * 2 + 1;
    public static final long ALIVE_TIME = 30;
    private static final BlockingDeque<Runnable> TASK_QUENE = new LinkedBlockingDeque<>(128);
    public static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "#SimpleAsyncTask " + mCount.getAndIncrement());
        }
    };

    public static final Executor threadPoolExecutor = new ThreadPoolExecutor(CORE_THREAD_NUM,
            MAX_THREAD_NUM,
            ALIVE_TIME,
            TimeUnit.SECONDS,
            TASK_QUENE,
            THREAD_FACTORY);

    public static final Executor defaultThreadPool = new Executor() {
        @Override
        public synchronized void execute(@NonNull Runnable command) {
            threadPoolExecutor.execute(command);
        }
    };

    public static final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            AsyncResult asyncTask = (AsyncResult) msg.obj;
            switch (msg.what) {
                case TASK_COMPLETE:
                    asyncTask.getTask().postExecute(asyncTask.result);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Task task;

    public SimpleAsyncTask() {
        task = new Task(this);
    }

    protected abstract void preExecute();

    protected abstract Result doInBackground(Params... params);

    protected abstract void postExecute(Result result);

    public void execute(Params... params) {
        executeInExecutor(defaultThreadPool, params);
    }

    public void executeInExecutor(Executor executor, Params... params){
        preExecute();
        task.setParams(params);
        executor.execute(task);
    }

    public static class AsyncResult{
        private SimpleAsyncTask task;
        private Object result;

        public AsyncResult(SimpleAsyncTask task, Object result) {
            this.task = task;
            this.result = result;
        }

        public SimpleAsyncTask getTask() {
            return task;
        }

        public Object getResult() {
            return result;
        }
    }

    public static class Task implements Runnable{
        private SimpleAsyncTask simpleAsyncTask;
        private Object[] params;
        public Task(SimpleAsyncTask asyncTask) {
            simpleAsyncTask = asyncTask;
        }

        public void setParams(Object... params){
            this.params = params;
        }

        @Override
        public void run() {
            Object result = simpleAsyncTask.doInBackground(params);
            AsyncResult asyncResult = new AsyncResult(simpleAsyncTask, result);
            Message message = new Message();
            message.obj = asyncResult;
            message.what = TASK_COMPLETE;
            SimpleAsyncTask.handler.sendMessage(message);
        }
    }
}
