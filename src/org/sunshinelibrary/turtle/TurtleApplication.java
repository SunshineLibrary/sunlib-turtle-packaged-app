package org.sunshinelibrary.turtle;

import android.app.Application;
import android.content.Intent;
import org.sunshinelibrary.turtle.init.InitService;

/**
 * Created with IntelliJ IDEA.
 * User: fengxiaoping
 * Date: 10/15/13
 * Time: 12:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class TurtleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Intent syncIntent = new Intent(this, InitService.class);
        startService(syncIntent);
    }
}
