package org.sunshinelibrary.turtle;

import android.app.Application;
import android.content.Intent;
import org.sunshinelibrary.turtle.syncservice.AppSyncService;
import org.sunshinelibrary.turtle.webservice.RestletWebService;

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

        Intent serverIntent = new Intent(this, RestletWebService.class);
        startService(serverIntent);

        Intent syncIntent = new Intent(this, AppSyncService.class);
        startService(syncIntent);

    }

}
