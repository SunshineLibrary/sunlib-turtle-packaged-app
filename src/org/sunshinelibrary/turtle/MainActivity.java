package org.sunshinelibrary.turtle;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.sunshinelibrary.turtle.dashboard.ServiceButton;
import org.sunshinelibrary.turtle.init.InitService;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.syncservice.AppSyncService;
import org.sunshinelibrary.turtle.taskmanager.TaskWithResult;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.ConnectionState;
import org.sunshinelibrary.turtle.utils.DateFormater;
import org.sunshinelibrary.turtle.utils.TurtleInfoUtils;
import org.sunshinelibrary.turtle.webservice.RestletWebService;

import java.util.*;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//    }
   Timer timer;
    TimerTask updateServiceStatus;
    ServiceButton syncButton;
    //    ServiceButton managerButton;
    ServiceButton webButton;
    Button checkServerButton;
//    TextView serverState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        syncButton = ((ServiceButton) findViewById(R.id.syncbutton));
        syncButton.init(AppSyncService.class);

        webButton = ((ServiceButton) findViewById(R.id.webbutton));
        webButton.init(RestletWebService.class);

        checkServerButton = ((Button) findViewById(R.id.checkserver));

        findViewById(R.id.checkserver).setOnClickListener(new CheckServer());
        findViewById(R.id.managerbutton).setOnClickListener(new ManagerListener());  //TODO:???-->initService
        findViewById(R.id.shutdown).setOnClickListener(new ShutdownListener());
        findViewById(R.id.deleteUserData).setOnClickListener(new DeleteUserDataListener());
        findViewById(R.id.exercise).setOnClickListener(new OpenSunExercise());
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

    public class DeleteUserDataListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TurtleManagers.userDataManager.deleteAll("");
        }
    }

    public class OpenSunExercise implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            try {
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("org.sunshinelibrary.exercise");
                startActivity(LaunchIntent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "无法找到阳光提高班", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class ShutdownListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("关闭阳光芸芸")
                    .setMessage("真的要关闭吗？")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityManager am = (ActivityManager) MainActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
                            List<ActivityManager.RunningServiceInfo> running = am.getRunningServices(Integer.MAX_VALUE);
                            List<String> servicesToStop = new ArrayList<String>();
                            servicesToStop.add(AppSyncService.class.getName());
                            servicesToStop.add(RestletWebService.class.getName());
                            for (ActivityManager.RunningServiceInfo service : running) {
                                if (servicesToStop.contains(service.service.getClassName())) {
                                    stopService(new Intent().setComponent(service.service));
                                }
                            }
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("不退出，按错了", null)
                    .show();
        }
    }

    public class ManagerListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent mServiceIntent = new Intent(MainActivity.this, InitService.class);
            startService(mServiceIntent);

//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://127.0.0.1:9460"));
//            browserIntent.setPackage("com.android.chrome");
//            startActivity(browserIntent);
        }
    }

    public class CheckServer implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            checkServerButton.setEnabled(false);
            new Thread(new CheckServerTask()).start();
        }

        public class CheckServerTask implements Runnable {

            @Override
            public void run() {
                final ConnectionState connectionState = TurtleInfoUtils.getLocalServerState();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(connectionState.toString())
                                .setTitle("服务器检查结果");
                        builder.create().show();
                        checkServerButton.setEnabled(true);
                    }
                });
            }
        }
    }

    class UpdateServiceTask extends TimerTask {

        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TurtleManagers.isInit) {
                        return;
                    }
                    syncButton.refresh();
                    webButton.refresh();
                    ((TextView) findViewById(R.id.textView)).setText(
                            "本机IP地址: " + TurtleInfoUtils.getIPAddress(true) +
                                    "\t服务状态最后更新时间: " +
                                    DateFormater.format(Calendar.getInstance().getTimeInMillis()));
//                    ((TextView) findViewById(R.id.turtleInfo)).setText(turtleInfo);
                    ((TextView) findViewById(R.id.accessToken)).setText(Configurations.getAccessToken());

                    if (Configurations.lastSuccessSync == 0) {
                        ((TextView) findViewById(R.id.lastSuccessSync)).setText("尚未完成过同步");
                    } else {
                        ((TextView) findViewById(R.id.lastSuccessSync)).setText(DateFormater.format(Configurations.lastSuccessSync));
                        findViewById(R.id.exercise).setVisibility(View.VISIBLE);
                    }

                    ((TextView) findViewById(R.id.lastSync)).setText(
                            ((Configurations.lastSync == 0) ? "尚未同步过" : DateFormater.format(Configurations.lastSync))
                    );

                    TaskWithResult currentTask = TurtleManagers.taskManager.peek();
                    if (currentTask != null) {
                        WebApp app = currentTask.getWebApp();
                        int progress = currentTask.getProgress();
                        ((TextView) findViewById(R.id.currentTask)).setText(
                                currentTask.getState() + "(" + progress + "%" + "): " + app.download_url);
                    } else {
                        ((TextView) findViewById(R.id.currentTask)).setText("当前无任务运行");
                    }
                }
            });
        }
    }

}
