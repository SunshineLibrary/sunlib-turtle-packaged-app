package org.sunshinelibrary.turtle;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;
import org.apache.commons.io.FileUtils;
import org.sunshinelibrary.turtle.appmanager.WebAppException;
import org.sunshinelibrary.turtle.syncservice.AppSyncService;
import org.sunshinelibrary.turtle.syncservice.SyncEvent;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.webservice.RestletWebService;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: fengxiaoping
 * Date: 10/15/13
 * Time: 12:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class TurtleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            TurtleManagers.init(this);
            TurtleManagers.appManager.refresh();
            if (TurtleManagers.appManager.getApp("0") == null) {
                initLauncherApp();
            }
        } catch (Exception e) {
            Logger.e("turtle initialized incorrect");
            e.printStackTrace();
            Toast.makeText(this, "turtle initialized incorrect", Toast.LENGTH_LONG).show();
            throw new RuntimeException();
        }

        Intent serverIntent = new Intent(this, RestletWebService.class);
        startService(serverIntent);
        Intent syncIntent = new Intent(this, AppSyncService.class);
        startService(syncIntent);

        startIntervalAlarm();
    }

    private void initLauncherApp() throws IOException, WebAppException {
        Logger.i("launcher not exits, install preinstall one");
        File tmpFile = File.createTempFile("turtle_", "tmp");
        FileUtils.copyInputStreamToFile(getAssets().open(Configurations.LAUNCHER_APP_FILE), tmpFile);
        TurtleManagers.appManager.installApp(tmpFile);
        FileUtils.writeStringToFile(
                new File(Configurations.getAppBase(), "heart"),
                "window.isTurtleOnline = true; console.log('turtle is on');");
    }

    public void startIntervalAlarm() {
        PendingIntent pendingIntent = SyncEvent.SYNC_START.createBroadcast(this);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), Configurations.SYNC_INTERVAL, pendingIntent);
        Logger.i("alarm started, interval is " + Configurations.SYNC_INTERVAL);
    }
}
