package org.sunshinelibrary.turtle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.sunshinelibrary.turtle.init.InitService;
import org.sunshinelibrary.turtle.syncservice.AppSyncService;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.webservice.RestletWebService;

/**
 * Created with IntelliJ IDEA.
 * User: fengxiaoping
 * Date: 10/14/13
 * Time: 10:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SyncTriggerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i("trigger sync");
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && !InitService.isRunning){
            Intent initIntent = new Intent(context, InitService.class);
            context.startService(initIntent);
        }else{
            try{
                TurtleManagers.userManager.login();
            }catch (NullPointerException e){
                e.printStackTrace();
                Logger.e("-=-=-=-=-=-=-> Current userManager is Null !!");
            }
        }
    }
}