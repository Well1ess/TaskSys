package nim.shs1330.netease.com.tasksys.poster;

import nim.shs1330.netease.com.tasksys.helper.EventsHelper;

/**
 * Created by shs1330 on 2017/9/29.
 */

public class AsyncPoster implements Runnable, Poster{
    private EventsHelper eventsHelper;
    private ExecuteCommand executeCommand;
    public AsyncPoster(EventsHelper eventsHelper) {
        this.eventsHelper = eventsHelper;
    }

    @Override
    public void sendMessage(Object o){
        this.executeCommand = (ExecuteCommand) o;
        eventsHelper.getExecutor().execute(this);
    }

    @Override
    public void run() {
        executeCommand.exeSu();
    }
}
