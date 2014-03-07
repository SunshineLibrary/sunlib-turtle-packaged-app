package org.sunshinelibrary.turtle.mixpanel;

import android.util.Log;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.postdata.PostDataTask;
import org.sunshinelibrary.turtle.utils.Configurations;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 14-3-6
 * Time: PM12:01
 */

public class MixpanelTask extends PostDataTask {

    private static final String TAG = "MixpanelTask";

    protected MixpanelTask(String target, String content){
        super(target,content);
    }

    @Override
    protected void upload() throws Exception{
        URL url = new URL(Configurations.getSunlibAPI(Configurations.SunAPI.MIXPANEL) + this.target);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(this.content);
        writer.flush();
        writer.close();
        os.close();
        conn.connect();
        Log.i(TAG,conn.getResponseMessage());
        if (conn.getResponseCode()>=300 || conn.getResponseCode()<200) {
            Log.e(TAG,"send mixpanel json failed, wait for next sync");
            return;
        }
        TurtleManagers.mixpanelManager.getPostDataQueue().remove();
    }
}
