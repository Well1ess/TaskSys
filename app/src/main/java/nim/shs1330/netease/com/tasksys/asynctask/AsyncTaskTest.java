package nim.shs1330.netease.com.tasksys.asynctask;

import android.util.Log;

/**
 * Created by 张丽华 on 2017/10/11.
 * Description:
 */

public class AsyncTaskTest extends SimpleAsyncTask {
    private static final String TAG = "AsyncTaskTest";
    @Override
    protected void preExecute() {
        Log.d(TAG, "preExecute: " );
    }

    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }

    @Override
    protected void postExecute(Object o) {

    }
}
