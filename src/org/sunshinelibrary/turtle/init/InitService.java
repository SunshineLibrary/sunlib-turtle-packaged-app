package org.sunshinelibrary.turtle.init;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
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
            TurtleManagers.init(this);//创建基本的.turtle/等目录
            //TODO:auto login 放在这里去登陆会和需要的生命周期是一致的么？也就是说会不会存在“当需要登陆服务的时候但是不会走这个方法”的情况
            TurtleManagers.loginTask();

            TurtleManagers.appManager.refresh();//mount完毕所有apps下面的folder
            if (TurtleManagers.appManager.getApp("0") == null) {//检测是否有0.zip，如果木有则需要弄一个，必须要有！应该是那个dashboard,显示所有是web而不是chapter，并且launchable的
                //initLauncherApp();
            }
            Log.i("Cong", "apps.length="+TurtleManagers.appManager.getAppsMap().size());
        } catch (Exception e) {
            Logger.e("turtle initialized incorrect," + e.getMessage());
            Toast.makeText(this, "turtle initialized incorrect", Toast.LENGTH_LONG).show();
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

        //apps加载完毕，基本folder就绪，0.zip完毕，isTurtleOn标志位写入完毕. 下面就启动两个服务
        Intent serverIntent = new Intent(this, RestletWebService.class);
        startService(serverIntent);
        Intent syncIntent = new Intent(this, AppSyncService.class);
        startService(syncIntent);

        startIntervalAlarm();
        stopSelf();
        //初始化服务执行完毕，结束自己
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
