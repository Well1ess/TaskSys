package nim.shs1330.netease.com.tasksys.helper;

import android.widget.Toast;

/**
 * Created by shs1330 on 2017/10/11.
 */

public class ToastHelper {
    public void show(String content){
        Toast.makeText(Client.getContext(), content, Toast.LENGTH_SHORT);
    }
}
