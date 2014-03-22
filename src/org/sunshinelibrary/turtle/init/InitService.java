package org.sunshinelibrary.turtle.init;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
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
public class InitService extends Service {

    public static boolean isLoginTaskRunning = false;
    public static boolean isRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Logger.i("--------------------->InitService Started !!!");
        isRunning = true;
        try {
            TurtleManagers.init(InitService.this);
            TurtleManagers.userManager.login(); //尝试携带本地信息去登录
            TurtleManagers.appManager.refresh();//mount完毕所有apps下面的folder

            // 内置 Mixpanel 文件夹，纯离线逻辑，率先进行初始化，以便统计所有事件
            if (TurtleManagers.appManager.getApp("mixpanel") == null) {
                initMixpanelApp();
            }

            // 内置 Login webapp, 用于用户初始登录，以便后续下载 wpk
            if (TurtleManagers.appManager.getApp("login") == null) {
                initLoginApp();
            }
        } catch (Exception e) {
            Logger.e("turtle initialized incorrect," + e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            FileUtils.writeStringToFile(//向heart文件写入window全局变量isTurtleOn，供后面exercise中检测此变量来查看turtle是否启动
                    new File(Configurations.getAppBase(), "heart"),
                    "window.isTurtleOnline = true; console.log('turtle is on');");
        } catch (IOException e) {
            Logger.e("cannot write bootloader response");
            throw new RuntimeException(e);
        }

        startIntervalAlarm();
        return Service.START_STICKY;
    }

    private void initMixpanelApp() throws IOException, WebAppException {
        Logger.i("Prepare install webapp-mixpanel");
        File tmpFile = File.createTempFile("turtle_", "tmp");
        FileUtils.copyInputStreamToFile(getAssets().open(Configurations.MIXPANEL_APP_FILE), tmpFile);
        TurtleManagers.appManager.installApp(tmpFile);

    }

    private void initLoginApp() throws IOException, WebAppException {
        Logger.i("Prepare install webapp-login");
        File tmpFile = File.createTempFile("turtle_", "tmp");
        FileUtils.copyInputStreamToFile(getAssets().open(Configurations.LOGIN_APP_FILE), tmpFile);
        TurtleManagers.appManager.installApp(tmpFile);
    }

    public void startIntervalAlarm() {
        PendingIntent pendingIntent = SyncEvent.SYNC_START.createBroadcast(this);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), Configurations.SYNC_INTERVAL, pendingIntent);
        Logger.i("alarm started, interval is " + Configurations.SYNC_INTERVAL);
    }
}
