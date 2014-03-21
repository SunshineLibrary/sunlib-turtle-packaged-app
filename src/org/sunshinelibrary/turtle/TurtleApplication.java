package org.sunshinelibrary.turtle;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.sunshinelibrary.turtle.init.InitService;
import org.sunshinelibrary.turtle.utils.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: fengxiaoping
 * Date: 10/15/13
 * Time: 12:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class TurtleApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.v("Application start");
        TurtleApplication.context = getApplicationContext();
        if(!InitService.isRunning){
            Intent intent = new Intent(this, InitService.class);
            startService(intent);
        }
    }

    public static Context getAppContext() {
        return TurtleApplication.context;
    }
}
