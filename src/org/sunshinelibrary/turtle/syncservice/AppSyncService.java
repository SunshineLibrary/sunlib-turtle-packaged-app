package org.sunshinelibrary.turtle.syncservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.models.DeleteTask;
import org.sunshinelibrary.turtle.models.DownloadTask;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.utils.Diff;
import org.sunshinelibrary.turtle.utils.DiffManifest;
import org.sunshinelibrary.turtle.utils.Logger;
import com.squareup.tape.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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

        @Override
        protected Integer doInBackground(Void... voids) {
            running = true;
            int successTask = 0;
            Logger.i("SyncTask start");
            // fetch apps.json
            // TODO change to real sync action
            List<WebApp> localApps = new ArrayList<WebApp>();
            List<WebApp> remoteApps = new ArrayList<WebApp>();

            // get diff part
            DiffManifest diffManifest = Diff.generateDiffTask(localApps, remoteApps);

            for (WebApp newApp : diffManifest.newApps) {
                TurtleManagers.taskManager.addTask(new DownloadTask(newApp));
            }

            for (WebApp deletedApp : diffManifest.deletedApps) {
                TurtleManagers.taskManager.addTask(new DeleteTask(deletedApp));
            }

            // do it one by one
            Queue<Task> tasks = TurtleManagers.taskManager.getAllTask();
            int total= 0;
            while (true) {
                Task task = tasks.peek();
                total++;
                if (task == null) {
                    break;
                }
                try {
                    task.execute(context);
                    successTask++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tasks.remove();
            }

//            while(true){
//                TurtleManagers.userDataManager.
//            }

            return successTask;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Logger.i("onPostExecute complete, success task " + result);
            running = false;
        }

    }
}
