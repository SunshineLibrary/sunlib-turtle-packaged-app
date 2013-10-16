package org.sunshinelibrary.turtle.models;

import android.content.Context;
import android.text.TextUtils;
import com.squareup.tape.Task;
import org.apache.commons.io.FileUtils;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.appmanager.WebAppException;
import org.sunshinelibrary.turtle.utils.Logger;

import java.io.File;
import java.net.URL;

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
        File tmpFile = null;
        try {
            // Download the app to temp directory
            tmpFile = File.createTempFile("turtle_", ".tmp");
            URL url = new URL(app.download_url);
            FileUtils.copyURLToFile(url, tmpFile);
            File zipFile = tmpFile;

            // Add to AppManager
            WebApp app = TurtleManagers.appManager.installApp(context, zipFile);
            appId = app.getId();
        } catch (Exception e) {
            // TODO to print a brief error, and clean up
            Logger.e("download task failed," + e.getMessage());
            e.printStackTrace();
            if (!TextUtils.isEmpty(appId)) {
                try {
                    TurtleManagers.appManager.uninstallApp(context, appId);
                } catch (WebAppException e1) {
                    e1.printStackTrace();
                }
            }
        }
        Logger.i("download task complete," + app);
    }

}
