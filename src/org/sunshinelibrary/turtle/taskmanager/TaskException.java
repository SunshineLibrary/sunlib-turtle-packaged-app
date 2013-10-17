package org.sunshinelibrary.turtle.taskmanager;

/**
 * User: fxp
 * Date: 10/17/13
 * Time: 12:39 PM
 */
public class TaskException extends Exception {

    public TaskException(String s) {
        super(s);
    }

    public TaskException(Exception e) {
        super(e);
    }
}
