package org.sunshinelibrary.turtle.taskmanager;

import java.util.Queue;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 9:17 PM
 */
public interface TaskManager {

    public WebAppTaskContext peek();

    public WebAppTaskContext remove();

    public void addTask(WebAppTaskContext task);

    public void removeTask(String id);

    public Queue<WebAppTaskContext> getAllTask();

    public void register(TaskManagerCallback callback);

    public void unregister(TaskManagerCallback callback);

}
