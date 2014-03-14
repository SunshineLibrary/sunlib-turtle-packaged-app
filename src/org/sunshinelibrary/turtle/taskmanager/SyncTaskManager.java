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
    private ConcurrentLinkedQueue<WebAppTask> tasks = new ConcurrentLinkedQueue<WebAppTask>();

    @Override
    public WebAppTask peek() {
        return tasks.peek();
    }

    @Override
    public WebAppTask remove() {
        WebAppTask ret = null;
        try{
            ret = tasks.remove();
            for (TaskManagerCallback callback : callbackList) {
                callback.onTaskChange();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void addTask(WebAppTask task) {
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
    public Queue<WebAppTask> getAllTask() {
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
