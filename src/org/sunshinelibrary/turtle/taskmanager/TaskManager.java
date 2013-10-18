package org.sunshinelibrary.turtle.taskmanager;

import java.util.Queue;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 9:17 PM
 */
public interface TaskManager {

    public TaskWithResult peek();

    public TaskWithResult remove();

    public void addTask(TaskWithResult task);

    public void removeTask(String id);

    public Queue<TaskWithResult> getAllTask();

    public void register(TaskManagerCallback callback);

    public void unregister(TaskManagerCallback callback);

}
