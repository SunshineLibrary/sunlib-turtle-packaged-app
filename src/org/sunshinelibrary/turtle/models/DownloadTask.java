package org.sunshinelibrary.turtle.models;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.appmanager.WebAppException;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.webservice.RestletWebService;
import com.squareup.tape.Task;

import java.io.File;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 9:16 PM
 */
public class DownloadTask implements Task<Context> {
    WebApp app;

    public DownloadTask(WebApp newApp) {
        app = newApp;
    }

    @Override
    public void execute(Context context) {
        // TODO implement this
        Logger.i("download this app start," + app);
        String appId = null;
        try {
            // Download the app to temp directory
            // TODO change to downloaded zip file
            File zipFile = new File("/sdcard/1.zip");

            // Add to AppManager
            WebApp app = TurtleManagers.appManager.installApp(context, zipFile);
            appId = app.getId();

            Intent intent = new Intent(context, RestletWebService.class);
            intent.setAction("install app");
            context.startService(intent);

        } catch (Exception e) {
            // TODO to print a brief error, and clean up
            e.printStackTrace();
            if (!TextUtils.isEmpty(appId)) {
                try {
                    TurtleManagers.appManager.uninstallApp(context, appId);
                } catch (WebAppException e1) {
                    e1.printStackTrace();
                }
            }
        }
        Logger.i("download this app complete," + app);
    }

}
