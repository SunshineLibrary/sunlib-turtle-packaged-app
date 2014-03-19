package org.sunshinelibrary.turtle.syncservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.mixpanel.MixpanelTask;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.taskmanager.DeleteTask;
import org.sunshinelibrary.turtle.taskmanager.DownloadTask;
import org.sunshinelibrary.turtle.taskmanager.WebAppTask;
import org.sunshinelibrary.turtle.userdatamanager.UserDataTask;
import org.sunshinelibrary.turtle.userdatamanager.UserDataTaskQueue;
import org.sunshinelibrary.turtle.mixpanel.MixpanelTaskQueue;

import org.sunshinelibrary.turtle.utils.*;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
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
        Logger.i("------------->isAppSyncServiceRunning==>"+running);
        Logger.i("------------->isOnline==>"+Configurations.isOnline(this));
        if (!running && Configurations.isOnline(this)) {
            new SyncTask().execute();
            running = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class SyncTask extends AsyncTask<Void, Integer, Integer> {

        //TODO: move getRemoteApps operation here
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        public Map<String, WebApp> getRemoteApps() {
            Map<String, WebApp> ret = null;
            InputStream in = null;
            try {
                HttpClient client = TurtleManagers.cookieManager.client;
                BasicHttpContext context = TurtleManagers.cookieManager.httpContext;
                BasicCookieStore cookieStore = TurtleManagers.cookieManager.cookieStore;
                if(cookieStore.getCookies().isEmpty()) {
                    Log.i("LiuCong", "Error: No Cookie");
                }
                context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

                String appsRequestUrl = Configurations.getSunlibAPI(Configurations.SunAPI.APPSJSON);
                HttpGet request = new HttpGet(appsRequestUrl);
                HttpResponse response = client.execute(request, context);

                in = response.getEntity().getContent();
                String manifest = IOUtils.toString(in);
                Type type = new TypeToken<List<WebApp>>() {
                }.getType();
                List<WebApp> remoteApps = new Gson().fromJson(manifest, type);
                ret = new HashMap<String, WebApp>();
                for (WebApp app : remoteApps) {
                  ret.put(app.getId(), app);
                }
            } catch (Exception e) {
                Logger.e("get remote apps failed," + e.getMessage());
            } finally {
                if (in != null) {
                    try {
                        IOUtils.closeQuietly(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.e("close connection failed");
                    }
                }
            }
            return ret;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int successTask = 0;
            Logger.i("SyncTask start");
            // fetch apps.json
            Map<String, WebApp> remoteApps = getRemoteApps();
            if (remoteApps == null) {
                Logger.i("fetch apps.json failed");
                running = false;
                return 0;
            }

            // get diff part
            DiffManifest diffManifest = Diff.generateDiffTask(
                    TurtleManagers.appManager.getAppsMap(),
                    remoteApps);

            Logger.i("diff manifest:" + diffManifest);

            for (WebApp newApp : diffManifest.newApps) {
                TurtleManagers.taskManager.addTask(new DownloadTask(newApp));
            }

            for (WebApp deletedApp : diffManifest.deletedApps) {
                TurtleManagers.taskManager.addTask(new DeleteTask(deletedApp));
            }

            Configurations.lastSync = Calendar.getInstance().getTimeInMillis();
            if (diffManifest.newApps.size() == 0 && diffManifest.deletedApps.size() == 0) {
                Configurations.lastSuccessSync = Calendar.getInstance().getTimeInMillis();
            }

            // do it one by one
            int total = 0;
            while (true) {
                WebAppTask task = TurtleManagers.taskManager.peek();
                total++;
                if (task == null) {
                    break;
                }
                try {
                    if(task.getClass() == DownloadTask.class &&
                            (TurtleInfoUtils.getAccessToken(AppSyncService.this) == null ||
                                    TurtleInfoUtils.getAccessToken(AppSyncService.this).equals(""))){
                        // 等待用户登录，以便个性化下载wpk
                    }else{
                        task.execute();
                        if (task.isOk()) {
                            successTask++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("task execute failed");
                }
                TurtleManagers.taskManager.remove();
            }

           /**
            *
            * Post Mixpanel Data.
            *
            */
            while (true) {
                try {
                    MixpanelTask task = ((MixpanelTaskQueue)TurtleManagers.mixpanelManager
                            .getPostDataQueue()).peek();
                    if (task == null) {
                        break;
                    }else{
                        Logger.i("ready to send mixpanel data");
                        task.execute("Mixpanel");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            /**
             *
             *  Post User Data.
             *
             */
            while (true) {
                try {
                    Logger.i("-------------->while true userdata task number:"+((UserDataTaskQueue)TurtleManagers.userDataManager
                            .getPostDataQueue()).size());
                    UserDataTask task = ((UserDataTaskQueue)TurtleManagers.userDataManager
                            .getPostDataQueue()).peek();
                    if (task == null) {
                        break;
                    }else{
                        Logger.i("ready to send user data");
                        task.execute("UserData");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            Logger.i("onPostExecute complete, success task " + successTask);
            running = false;
            return 0;
        }

    }
}