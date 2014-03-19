package org.sunshinelibrary.turtle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.sunshinelibrary.turtle.appmanager.AppManager;
import org.sunshinelibrary.turtle.appmanager.WebAppManager;
import org.sunshinelibrary.turtle.mixpanel.MixpanelDataManager;
import org.sunshinelibrary.turtle.taskmanager.SyncTaskManager;
import org.sunshinelibrary.turtle.taskmanager.TaskManager;
import org.sunshinelibrary.turtle.user.User;
import org.sunshinelibrary.turtle.userdatamanager.TapeUserDataManager;
import org.sunshinelibrary.turtle.userdatamanager.UserDataManager;
import org.sunshinelibrary.turtle.user.UserManager;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.utils.StreamToString;
import org.sunshinelibrary.turtle.utils.TurtleInfoUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 3:27 PM
 */
public class TurtleManagers {
    public static AppManager appManager;
    public static TaskManager taskManager;
    public static UserDataManager userDataManager;
    public static UserManager userManager;
    public static CookieManager cookieManager;
    public static MixpanelDataManager mixpanelManager;
    public static boolean isInit = false;

    public static void init(Context context) throws Exception {
        SharedPreferences settings = context.getSharedPreferences(Configurations.TURTLE_SHARED_PREFERENCE, 0);
        if (!settings.getBoolean(Configurations.TURTLE_SHARED_PREFERENCE_INIT, false)) {
            // Delete all turtle folder exists
            //FileUtils.deleteDirectory(new File(Configurations.getStorageBase()));
            Logger.i("first start, clean all files in turtle folder");
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Configurations.TURTLE_SHARED_PREFERENCE_INIT, true);
            editor.commit();
        } else {
            Logger.i("turtle already installed, maybe upgrade");
        }

        Configurations.init(context);
        appManager = new WebAppManager(context);
        taskManager = new SyncTaskManager();
        userDataManager = new TapeUserDataManager(context);
        userManager = new UserManager(context);
        mixpanelManager = new MixpanelDataManager();
        cookieManager = new CookieManager();
        isInit = true;
    }
}
