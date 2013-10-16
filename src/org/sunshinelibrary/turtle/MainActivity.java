package org.sunshinelibrary.turtle;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;
import org.sunshinelibrary.turtle.syncservice.SyncEvent;
import org.sunshinelibrary.turtle.utils.Logger;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.context = this;

        findViewById(R.id.button).setOnClickListener(new AddClick());
        findViewById(R.id.button1).setOnClickListener(new QueryClick());

        try {
            TurtleManagers.init();
        } catch (Exception e) {
            Logger.e("manager not initialized correct");
            e.printStackTrace();

            // TODO force close application
            throw new RuntimeException();
        }
        startIntervalAlarm();
    }

    public void startIntervalAlarm() {
        PendingIntent pendingIntent = SyncEvent.SYNC_START.createBroadcast(this);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), 30 * 1000, pendingIntent);
        Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();
        Logger.i("alarm started");
    }

    public class AddClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            WebApp app = new WebApp(context);
//            app.localFolder = "123";
//            app.id = "id123";
//            app.save();
//            onDao dao = new PersonDao(getContext());
        }
    }

    public class QueryClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            List<WebApp> apps = WebApp.getAll();
//            Log.i("WebAppManager",
//                    new Gson().toJson(WebApp.getById("id123"))
//                    new Gson().toJson(apps)
//            );

        }
    }

}
