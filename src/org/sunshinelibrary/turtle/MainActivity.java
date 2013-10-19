package org.sunshinelibrary.turtle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import org.sunshinelibrary.turtle.dashboard.ServiceButton;
import org.sunshinelibrary.turtle.syncservice.AppSyncService;
import org.sunshinelibrary.turtle.taskmanager.TaskManagerCallback;
import org.sunshinelibrary.turtle.taskmanager.TaskWithResult;
import org.sunshinelibrary.turtle.webservice.RestletWebService;

import java.text.SimpleDateFormat;
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

    public static String getNowTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm:ss");
        return "最后更新时间: " + sdf.format(cal.getTime());
    }

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

    class UpdateServiceTask extends TimerTask {

        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    syncButton.refresh();
                    webButton.refresh();
                    ((TextView) findViewById(R.id.textView)).setText(getNowTime());
//                    ((TextView) findViewById(R.id.turtleInfo)).setText(turtleInfo);
                    TaskWithResult currentTask = TurtleManagers.taskManager.peek();
                    if (currentTask != null) {
                        ((TextView) findViewById(R.id.textView2)).setText(
                                currentTask.getWebApp() + ":" + currentTask.getProgress());
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
