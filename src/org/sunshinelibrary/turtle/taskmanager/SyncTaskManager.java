package org.sunshinelibrary.turtle.taskmanager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 3:30 PM
 */
public class SyncTaskManager implements TaskManager {

    ConcurrentLinkedQueue<TaskWithResult> tasks = new ConcurrentLinkedQueue<TaskWithResult>();

    @Override
    public void addTask(TaskWithResult task) {
        tasks.add(task);
    }

    @Override
    public void removeTask(String id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queue<TaskWithResult> getAllTask() {
        return tasks;
    }
}
