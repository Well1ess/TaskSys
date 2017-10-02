package nim.shs1330.netease.com.tasksys.poster;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by shs1330 on 2017/9/28.
 */

public class MainPoster extends Handler implements Poster{

    public MainPoster(Looper looper) {
        super(looper);
    }

    @Override
    public void sendMessage(Object o) {
        Message message = new Message();
        message.obj = o;
        sendMessage(message);
    }

    @Override
    public void handleMessage(Message msg) {
        ((ExecuteCommand) msg.obj).exeSu();
    }

}
