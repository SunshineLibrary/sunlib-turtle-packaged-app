package org.sunshinelibrary.turtle.taskmanager;

import android.content.Context;
import org.sunshinelibrary.turtle.models.WebApp;

/**
 * Created with IntelliJ IDEA.
 * User: fengxiaoping
 * Date: 10/20/13
 * Time: 1:22 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class WebAppTaskContext implements TaskWithResult<Context> {
    /**
     * Task state
     */
    public String state = null;
    public int progress = 0;
    public WebApp app;
    /**
     * Whether the app is downloaded
     */
    boolean isOk = false;
    /**
     * Task result
     */
    Object result = null;

    public abstract void execute();

    @Override
    public boolean isOk() {
        return isOk;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getResult() {
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getState() {
        return state;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getProgress() {
        return progress;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public WebApp getWebApp() {
        return app;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
