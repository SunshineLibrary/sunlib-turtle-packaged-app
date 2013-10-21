package org.sunshinelibrary.turtle;

import android.content.Context;
import org.sunshinelibrary.turtle.appmanager.AppManager;
import org.sunshinelibrary.turtle.appmanager.WebAppManager;
import org.sunshinelibrary.turtle.taskmanager.SyncTaskManager;
import org.sunshinelibrary.turtle.taskmanager.TaskManager;
import org.sunshinelibrary.turtle.userdatamanager.TapeUserDataManager;
import org.sunshinelibrary.turtle.userdatamanager.UserDataManager;
import org.sunshinelibrary.turtle.utils.Configurations;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 3:27 PM
 */
public class TurtleManagers {
    public static AppManager appManager;
    public static TaskManager taskManager;
    public static UserDataManager userDataManager;

    public static void init(Context context) throws Exception {
        Configurations.init(context);
        appManager = new WebAppManager(context);
        taskManager = new SyncTaskManager();
        userDataManager = new TapeUserDataManager(context);
    }

}
