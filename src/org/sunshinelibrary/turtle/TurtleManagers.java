package org.sunshinelibrary.turtle;

import android.content.Context;
import android.content.SharedPreferences;
import org.apache.commons.io.FileUtils;
import org.sunshinelibrary.turtle.appmanager.AppManager;
import org.sunshinelibrary.turtle.appmanager.WebAppManager;
import org.sunshinelibrary.turtle.taskmanager.SyncTaskManager;
import org.sunshinelibrary.turtle.taskmanager.TaskManager;
import org.sunshinelibrary.turtle.userdatamanager.TapeUserDataManager;
import org.sunshinelibrary.turtle.userdatamanager.UserDataManager;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;

import java.io.File;

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
        SharedPreferences settings = context.getSharedPreferences(Configurations.TURTLE_SHARED_PREFERENCE, 0);
        if (!settings.getBoolean(Configurations.TURTLE_SHARED_PREFERENCE_INIT, false)) {
            // Delete all turtle folder exists
            FileUtils.deleteDirectory(new File(Configurations.getStorageBase()));
            Logger.i("first start, clean all files in turtle folder");
            settings.edit().putBoolean(Configurations.TURTLE_SHARED_PREFERENCE_INIT, true);
            settings.edit().commit();
        } else {
            Logger.i("turtle already installed, maybe upgrade");
        }

        Configurations.init(context);
        appManager = new WebAppManager(context);
        taskManager = new SyncTaskManager();
        userDataManager = new TapeUserDataManager(context);
    }

}
