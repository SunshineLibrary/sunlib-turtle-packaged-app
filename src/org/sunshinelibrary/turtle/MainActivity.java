package org.sunshinelibrary.turtle;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.sunshinelibrary.turtle.dashboard.ServiceButton;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.syncservice.AppSyncService;
import org.sunshinelibrary.turtle.taskmanager.TaskManagerCallback;
import org.sunshinelibrary.turtle.taskmanager.TaskWithResult;
import org.sunshinelibrary.turtle.utils.ConnectionState;
import org.sunshinelibrary.turtle.utils.DateFormater;
import org.sunshinelibrary.turtle.utils.TurtleInfoUtils;
import org.sunshinelibrary.turtle.webservice.RestletWebService;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    Timer timer;
    TimerTask updateServiceStatus;
    ServiceButton syncButton;
    ServiceButton webButton;
    Button checkServerButton;
//    TextView serverState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TurtleManagers.taskManager.register(new TaskManagerCallback() {
            @Override
            public void onTaskChange() {

            }
        });

        syncButton = ((ServiceButton) findViewById(R.id.syncbutton));
        syncButton.init(AppSyncService.class);

        webButton = ((ServiceButton) findViewById(R.id.webbutton));
        webButton.init(RestletWebService.class);

        checkServerButton = ((Button) findViewById(R.id.checkserver));

        findViewById(R.id.checkserver).setOnClickListener(new CheckServer());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (updateServiceStatus != null) {
            updateServiceStatus.cancel();
            updateServiceStatus = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        updateServiceStatus = new UpdateServiceTask();
        timer.scheduleAtFixedRate(updateServiceStatus, 0, 3000);
    }

    public class CheckServer implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            checkServerButton.setEnabled(false);
            new CheckServerTask().execute();
        }

        public class CheckServerTask extends AsyncTask<Void, Void, ConnectionState> {

            @Override
            protected ConnectionState doInBackground(Void... voids) {
                return TurtleInfoUtils.getLocalServerState();
            }

            @Override
            protected void onPostExecute(ConnectionState connectionState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(connectionState.toString())
                        .setTitle("服务器检查结果"
                        );
                builder.create().show();
                checkServerButton.setEnabled(true);
            }
        }
    }

    class UpdateServiceTask extends TimerTask {

        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    syncButton.refresh();
                    webButton.refresh();
                    ((TextView) findViewById(R.id.textView)).setText(
                            "本机IP地址: " + TurtleInfoUtils.getIPAddress(true) +
                                    "\t服务状态最后更新时间: " +
                                    DateFormater.format(Calendar.getInstance().getTimeInMillis()));
//                    ((TextView) findViewById(R.id.turtleInfo)).setText(turtleInfo);
                    TaskWithResult currentTask = TurtleManagers.taskManager.peek();
                    if (currentTask != null) {
                        WebApp app = currentTask.getWebApp();
                        int progress = currentTask.getProgress();
                        ((TextView) findViewById(R.id.currentTask)).setText(
                                app.download_url + ":" + progress);
                    } else {
                        ((TextView) findViewById(R.id.currentTask)).setText("");
                    }
                }
            });
        }
    }

//    private class TaskAdapter extends BaseAdapter {
//        private ConcurrentLinkedQueue<TaskWithResult> tasks = (ConcurrentLinkedQueue<TaskWithResult>) TurtleManagers.taskManager.getAllTask();
//
//        @Override
//        public int getCount() {
//            return tasks.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return tasks.toArray()[position];
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = View.inflate(MainActivity.this, R.layout.item, null);
//            TextView textView = (TextView) view.findViewById(R.id.textView);
//            textView.setText(tasks.toArray()[position].toString());
//            return view;
//        }
//    }
}
