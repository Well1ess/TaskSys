package nim.shs1330.netease.com.tasksys.asynctask;

import android.util.Log;

import nim.shs1330.netease.com.tasksys.helper.Client;
import nim.shs1330.netease.com.tasksys.helper.LogThreadNameUtil;

/**
 * Created by 张丽华 on 2017/10/11.
 * Description:
 */

public class AsyncTaskTest extends SimpleAsyncTask<Integer, Integer, Integer> {
    private static final String TAG = "AsyncTaskTest";
    @Override
    protected void preExecute() {
        Client.getHelper(LogThreadNameUtil.class).printThreadName();
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        Client.getHelper(LogThreadNameUtil.class).printThreadName();
        for (int i = 0; i< params[0]; i++){
            publishProgress(i);
        }
        return params[0] - 1;
    }

    @Override
    protected void postExecute(Integer integer) {
        Client.getHelper(LogThreadNameUtil.class).printThreadName();
    }

    @Override
    protected void updateProgress(Integer value) {
        Client.getHelper(LogThreadNameUtil.class).printThreadName();
        Log.d(TAG, "updateProgress: " + value);
    }

}
