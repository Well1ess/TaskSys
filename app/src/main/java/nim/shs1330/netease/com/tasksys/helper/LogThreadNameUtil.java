package nim.shs1330.netease.com.tasksys.helper;

import android.util.Log;

/**
 * Created by 张丽华 on 2017/10/11.
 * Description:
 */

public class LogThreadNameUtil {
    private static final String TAG = "ThreadNameUtil";
    public void printThreadName(){
        Log.d(TAG, "printThreadName: " + Thread.currentThread().getStackTrace()[0].getMethodName());
    }
}
