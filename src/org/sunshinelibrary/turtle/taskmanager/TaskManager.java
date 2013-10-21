package org.sunshinelibrary.turtle.taskmanager;

import java.util.Queue;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 9:17 PM
 */
public interface TaskManager {

    public WebAppTask peek();

    public WebAppTask remove();

    public void addTask(WebAppTask task);

    public void removeTask(String id);

    public Queue<WebAppTask> getAllTask();

    public void register(TaskManagerCallback callback);

    public void unregister(TaskManagerCallback callback);

}
