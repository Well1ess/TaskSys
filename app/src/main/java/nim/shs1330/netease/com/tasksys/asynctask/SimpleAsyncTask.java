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

    public static final Handler handler = new Handler(Looper.getMainLooper()){
        public static final int TASK_COMPLETE = 0x11;
        @Override
        public void handleMessage(Message msg) {
            SimpleAsyncTask asyncTask = (SimpleAsyncTask) msg.obj;
            switch (msg.what){
                case TASK_COMPLETE:
                    asyncTask.postExecute(null);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected abstract void preExecute(Params... params);

    protected abstract Result doInBackground(Params... params);

    protected abstract void postExecute(Result result);

    public void execute(Params... params) {

    }
}
