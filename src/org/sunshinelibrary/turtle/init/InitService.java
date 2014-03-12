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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        try {
            TurtleManagers.init(InitService.this);
            TurtleManagers.userManager.login(); //尝试携带本地信息去登录
            TurtleManagers.appManager.refresh();//mount完毕所有apps下面的folder
            if (TurtleManagers.appManager.getApp("0") == null) {//检测是否有0.zip，如果木有则需要弄一个，必须要有！应该是那个dashboard,显示所有是web而不是chapter，并且launchable的
                //initLauncherApp();
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

        //apps加载完毕，基本folder就绪，0.zip完毕，isTurtleOn标志位写入完毕.启动基础服务
        Intent serverIntent = new Intent(this, RestletWebService.class);
        startService(serverIntent);
        Intent syncIntent = new Intent(this, AppSyncService.class);
        startService(syncIntent);
        startIntervalAlarm();
        return Service.START_STICKY;
    }

    private void initLauncherApp() throws IOException, WebAppException {//如果没有搞定0.zip那么上面就会导致tyr..catch中抛出Exception
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
