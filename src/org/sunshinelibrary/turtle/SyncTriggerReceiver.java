package org.sunshinelibrary.turtle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
        Intent syncIntent = new Intent(context, AppSyncService.class);
        context.startService(syncIntent);

        Intent webIntent = new Intent(context, RestletWebService.class);
        context.startService(webIntent);

    }
}