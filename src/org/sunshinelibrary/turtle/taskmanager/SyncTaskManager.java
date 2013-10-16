package org.sunshinelibrary.turtle.taskmanager;

import com.squareup.tape.Task;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 3:30 PM
 */
public class SyncTaskManager implements TaskManager {

    ConcurrentLinkedQueue<Task> tasks = new ConcurrentLinkedQueue<Task>();

    @Override
    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public void removeTask(String id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queue<Task> getAllTask() {
        return tasks;
    }
}
