package org.sunshinelibrary.turtle.models;

import android.content.Context;
import android.os.SystemClock;
import org.sunshinelibrary.turtle.utils.Logger;
import com.squareup.tape.Task;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 9:16 PM
 */
public class DeleteTask implements Task<Context> {
    WebApp app;

    public DeleteTask(WebApp deletedApp) {
        app = deletedApp;
    }

    @Override
    public void execute(Context context) {
        // TODO implement this
        Logger.i("delete this app start," + app);
        SystemClock.sleep(1000);
        Logger.i("delete this app complete," + app);
    }
}
