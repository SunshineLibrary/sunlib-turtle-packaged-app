package org.sunshinelibrary.turtle.taskmanager;

import android.text.TextUtils;
import org.apache.commons.io.FileUtils;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.appmanager.WebAppException;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 9:16 PM
 */
public class DownloadTask extends WebAppTask {

    public DownloadTask(WebApp newApp) {
        app = newApp;
    }

    @Override
    public boolean isOk() {
        return isOk;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public String getState() {
        return state;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public WebApp getWebApp() {
        return app;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute() {
        Logger.i("download this app start," + app);
        String appId = null;
        File tmpFile = null;
        try {
            state = "downloading";
//            app.download_url = app.download_url;
            // Download the app to temp directory
            tmpFile = File.createTempFile("turtle_", ".tmp");
            if (!app.download_url.startsWith("http://")) {
                app.download_url = Configurations.serverHost + app.download_url;
            }
            URL url = new URL(app.download_url);
            downloadFileFromUrl(url, tmpFile);
//            FileUtils.copyURLToFile(url, tmpFile);
            File zipFile = tmpFile;

            state = "installing";
            // Add to AppManager
            progress = 0;
            WebApp app = TurtleManagers.appManager.installApp(zipFile);
            progress = 100;
            result = app;
            isOk = true;
            state = "completed";
            Logger.i("app downloaded and installed," + app);
        } catch (Exception e) {
            progress = 0;
            state = "failed";
            result = e.getMessage();

            Logger.e("download task failed," + e.getMessage());
            if (tmpFile != null) {
                FileUtils.deleteQuietly(tmpFile);
            }
            if (!TextUtils.isEmpty(appId)) {
                try {
                    TurtleManagers.appManager.uninstallApp(appId);
                } catch (WebAppException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void downloadFileFromUrl(URL url, File dstFile) throws IOException {
        int count;
        URLConnection conection = url.openConnection();
        conection.connect();
        // getting file length
        int lenghtOfFile = conection.getContentLength();
        // input stream to read file - with 8k buffer
        InputStream input = new BufferedInputStream(url.openStream(), 8192);
        // Output stream to write file
        OutputStream output = new FileOutputStream(dstFile);
        byte data[] = new byte[1024 * 4];
        long total = 0;
        while ((count = input.read(data)) != -1) {
            total += count;
            // publishing the progress....
            // After this onProgressUpdate will be called
            progress = (int) ((total * 100) / lenghtOfFile);
            // writing data to file
            output.write(data, 0, count);
        }
        // flushing output
        output.flush();
        // closing streams
        output.close();
        input.close();
    }

}
