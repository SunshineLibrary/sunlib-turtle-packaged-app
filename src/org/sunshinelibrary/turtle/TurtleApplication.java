package org.sunshinelibrary.turtle;

import android.app.Application;
import android.content.Intent;
import org.apache.commons.io.FileUtils;
import org.sunshinelibrary.turtle.syncservice.AppSyncService;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.webservice.RestletWebService;

import java.io.File;

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
            TurtleManagers.init();
            TurtleManagers.appManager.refresh();
            File tmpFile = File.createTempFile("turtle_", "tmp");
            FileUtils.copyInputStreamToFile(getAssets().open("0.zip"), tmpFile);
            TurtleManagers.appManager.installApp(this, tmpFile);
        } catch (Exception e) {
            Logger.e("turtle initialized correct");
            e.printStackTrace();
            // TODO force close application
            throw new RuntimeException();
        }

        Intent serverIntent = new Intent(this, RestletWebService.class);
        startService(serverIntent);

        Intent syncIntent = new Intent(this, AppSyncService.class);
        startService(syncIntent);

    }

}
