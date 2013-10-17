package org.sunshinelibrary.turtle.taskmanager;

import java.util.Queue;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 9:17 PM
 */
public interface TaskManager {

    public void addTask(TaskWithResult task);

    public void removeTask(String id);

    public Queue<TaskWithResult> getAllTask();

}
