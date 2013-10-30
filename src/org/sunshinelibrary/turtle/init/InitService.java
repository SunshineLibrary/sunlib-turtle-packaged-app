package org.sunshinelibrary.turtle.init;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;
import org.apache.commons.io.FileUtils;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.appmanager.WebAppException;
import org.sunshinelibrary.turtle.syncservice.AppSyncService;
import org.sunshinelibrary.turtle.syncservice.SyncEvent;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.webservice.RestletWebService;

import java.io.File;
import java.io.IOException;

/**
 * User: fxp
 * Date: 10/30/13
 * Time: 5:27 PM
 */
public class InitService extends IntentService {

    public InitService() {
        super("init");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (TurtleManagers.isInit) {
            Logger.i("TurtleManagers have initialized, please do it again.");
            stopSelf();
            return;
        }
        try {
            TurtleManagers.init(this);
            TurtleManagers.appManager.refresh();
            if (TurtleManagers.appManager.getApp("0") == null) {
                initLauncherApp();
            }
        } catch (Exception e) {
            Logger.e("turtle initialized incorrect," + e.getMessage());
            Toast.makeText(this, "turtle initialized incorrect", Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }

        try {
            FileUtils.writeStringToFile(
                    new File(Configurations.getAppBase(), "heart"),
                    "window.isTurtleOnline = true; console.log('turtle is on');");
        } catch (IOException e) {
            Logger.e("cannot write bootloader response");
            throw new RuntimeException(e);
        }

        Intent serverIntent = new Intent(this, RestletWebService.class);
        startService(serverIntent);
        Intent syncIntent = new Intent(this, AppSyncService.class);
        startService(syncIntent);

        startIntervalAlarm();
        stopSelf();
    }

    private void initLauncherApp() throws IOException, WebAppException {
        Logger.i("launcher not exits, install preinstall one");
        File tmpFile = File.createTempFile("turtle_", "tmp");
        FileUtils.copyInputStreamToFile(getAssets().open(Configurations.LAUNCHER_APP_FILE), tmpFile);
        TurtleManagers.appManager.installApp(tmpFile);
    }

    public void startIntervalAlarm() {
        PendingIntent pendingIntent = SyncEvent.SYNC_START.createBroadcast(this);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), Configurations.SYNC_INTERVAL, pendingIntent);
        Logger.i("alarm started, interval is " + Configurations.SYNC_INTERVAL);
    }
}
