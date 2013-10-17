package org.sunshinelibrary.turtle.taskmanager;

import com.squareup.tape.Task;

/**
 * User: fxp
 * Date: 10/17/13
 * Time: 12:42 PM
 */
public interface TaskWithResult<T> extends Task<T> {

    public boolean isOk();

    public Object getResult();
}
