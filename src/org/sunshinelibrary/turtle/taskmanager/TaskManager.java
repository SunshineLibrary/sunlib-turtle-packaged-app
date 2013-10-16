package org.sunshinelibrary.turtle.taskmanager;

import com.squareup.tape.Task;

import java.util.Queue;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 9:17 PM
 */
public interface TaskManager {

    public void addTask(Task task);

    public void removeTask(String id);

    public Queue<Task> getAllTask();

}
