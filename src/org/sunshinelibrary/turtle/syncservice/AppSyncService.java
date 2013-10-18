package org.sunshinelibrary.turtle.syncservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.taskmanager.DeleteTask;
import org.sunshinelibrary.turtle.taskmanager.DownloadTask;
import org.sunshinelibrary.turtle.taskmanager.TaskWithResult;
import org.sunshinelibrary.turtle.userdatamanager.UserDataTask;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Diff;
import org.sunshinelibrary.turtle.utils.DiffManifest;
import org.sunshinelibrary.turtle.utils.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 1:53 PM
 */
public class AppSyncService extends Service {

    static boolean running = false;
    static Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!running) {
            new SyncTask().execute();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static class SyncTask extends AsyncTask<Void, Integer, Integer> {

        public Map<String, WebApp> getRemoteApps() {
            Map<String, WebApp> ret = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(Configurations.getSunlibAPI(Configurations.SunAPI.APPSJSON));
                urlConnection = (HttpURLConnection) url.openConnection();
                String manifest = IOUtils.toString(urlConnection.getInputStream());
                Type type = new TypeToken<List<WebApp>>() {
                }.getType();
                List<WebApp> remoteApps = new Gson().fromJson(manifest, type);
                ret = new HashMap<String, WebApp>();
                for (WebApp app : remoteApps) {
                    ret.put(app.getId(), app);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return ret;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            running = true;
            int successTask = 0;
            Logger.i("SyncTask start");
            // fetch apps.json
            // TODO change to real sync action
            Map<String, WebApp> remoteApps = getRemoteApps();
            if (remoteApps == null) {
                Logger.i("fetch apps.json failed");
                return 0;
            }

            // get diff part
            DiffManifest diffManifest = Diff.generateDiffTask(
                    TurtleManagers.appManager.getAppsMap(),
                    remoteApps);

            for (WebApp newApp : diffManifest.newApps) {
                TurtleManagers.taskManager.addTask(new DownloadTask(newApp));
            }

            for (WebApp deletedApp : diffManifest.deletedApps) {
                TurtleManagers.taskManager.addTask(new DeleteTask(deletedApp));
            }

            // do it one by one
            int total = 0;
            while (true) {
                TaskWithResult task = TurtleManagers.taskManager.peek();
                total++;
                if (task == null) {
                    break;
                }
                try {
                    task.execute(context);
                    if (task.isOk()) {
                        successTask++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("task execute failed");
                }
                TurtleManagers.taskManager.remove();
            }

            while (true) {
                UserDataTask task = TurtleManagers.userDataManager.getUserDataQueue().peek();
                if (task == null) {
                    break;
                }
                URL url = null;
                try {
                    url = new URL(Configurations.getSunlibAPI(Configurations.SunAPI.USERDATA) + task.target);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Access-Token", task.accessToken);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(task.content);
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();
                    Logger.i(conn.getResponseMessage());
                    if (conn.getResponseCode() != 200) {
                        Logger.e("send userdata failed,wait for next sync");
                        break;
                    }
                    TurtleManagers.userDataManager.getUserDataQueue().remove();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return successTask;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Logger.i("onPostExecute complete, success task " + result);
            running = false;
        }

    }
}
