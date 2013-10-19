package org.sunshinelibrary.turtle.taskmanager;

import org.sunshinelibrary.turtle.models.WebApp;

/**
 * User: fxp
 * Date: 10/17/13
 * Time: 12:42 PM
 */
public interface TaskWithResult<T> {

    public boolean isOk();

    public Object getResult();

    public String getState();

    public int getProgress();

    public WebApp getWebApp();

}
