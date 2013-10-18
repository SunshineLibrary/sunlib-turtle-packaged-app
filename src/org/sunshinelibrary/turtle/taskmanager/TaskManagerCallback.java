package org.sunshinelibrary.turtle.taskmanager;

/**
 * User: fxp
 * Date: 10/18/13
 * Time: 12:04 PM
 */
public interface TaskManagerCallback {

    public void onNewTask(TaskWithResult task);

    public void onCompleteTask(TaskWithResult task);

}
