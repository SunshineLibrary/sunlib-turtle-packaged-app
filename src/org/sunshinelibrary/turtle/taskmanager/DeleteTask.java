package org.sunshinelibrary.turtle.taskmanager;

import android.content.Context;
import android.os.SystemClock;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.appmanager.WebAppException;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.utils.Logger;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 9:16 PM
 */
public class DeleteTask extends WebAppTaskContext {
    WebApp app;
    boolean isOk = false;
    Object result = null;

    public DeleteTask(WebApp deletedApp) {
        app = deletedApp;
    }


    @Override
    public boolean isOk() {
        return isOk;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void execute() {
        Logger.i("delete this app start," + app);
        try {
            TurtleManagers.appManager.uninstallApp(app.getId());
            Logger.i("app uninstall success," + app);
        } catch (WebAppException e) {
            e.printStackTrace();
            Logger.i("app uninstall failed," + app);
        }
        SystemClock.sleep(1000);
        Logger.i("delete this app complete," + app);
    }
}
