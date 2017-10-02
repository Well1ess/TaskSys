package nim.shs1330.netease.com.tasksys.poster;

import java.util.ArrayList;
import java.util.List;

import nim.shs1330.netease.com.tasksys.helper.EventsHelper;

/**
 * Created by 张丽华 on 2017/10/2.
 * Description:
 */

public class BackgroundPoster implements Poster, Runnable {

    private EventsHelper eventsHelper;
    private boolean isRunning = false;
    private List<Object> eventsQueue;

    public BackgroundPoster(EventsHelper eventsHelper) {
        this.eventsHelper = eventsHelper;
        this.eventsQueue = new ArrayList<>();
    }

    @Override
    public void sendMessage(Object o) {
        if (!isRunning) {
            isRunning = true;
            eventsHelper.getExecutor().execute(this);
        }
        eventsQueue.add(o);
    }

    @Override
    public void run() {
        if (eventsQueue.size() != 0) {
            Object events = eventsQueue.remove(0);
            ((ExecuteCommand) events).exeSu();
        }
    }
}
