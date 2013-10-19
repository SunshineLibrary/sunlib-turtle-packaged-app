package org.sunshinelibrary.turtle.taskmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 3:30 PM
 */
public class SyncTaskManager implements TaskManager {

    List<TaskManagerCallback> callbackList = new ArrayList<TaskManagerCallback>();
    private ConcurrentLinkedQueue<WebAppTaskContext> tasks = new ConcurrentLinkedQueue<WebAppTaskContext>();

    @Override
    public WebAppTaskContext peek() {
        return tasks.peek();
    }

    @Override
    public WebAppTaskContext remove() {
        WebAppTaskContext ret = tasks.remove();
        for (TaskManagerCallback callback : callbackList) {
            callback.onTaskChange();
        }
        return ret;
    }

    @Override
    public void addTask(WebAppTaskContext task) {
        tasks.add(task);
        for (TaskManagerCallback callback : callbackList) {
            callback.onTaskChange();
        }
    }

    @Override
    public void removeTask(String id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queue<WebAppTaskContext> getAllTask() {
        return tasks;
    }

    @Override
    public void register(TaskManagerCallback callback) {
        callbackList.add(callback);
    }

    @Override
    public void unregister(TaskManagerCallback callback) {
        callbackList.remove(callback);
    }
}
