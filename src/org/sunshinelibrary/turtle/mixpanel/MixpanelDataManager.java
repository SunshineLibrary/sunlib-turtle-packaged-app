package org.sunshinelibrary.turtle.mixpanel;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.sunshinelibrary.turtle.TurtleApplication;
import org.sunshinelibrary.turtle.userdatamanager.UserDataManager;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.GsonConverter;
import org.sunshinelibrary.turtle.utils.TolerantQueue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 14-3-5
 * Time: PM4:59
 */

public class MixpanelDataManager implements UserDataManager<MixpanelTaskQueue> {
    private static final String TAG = "MixpanelDataManager";
    public MixpanelTaskQueue mixpanelTaskQueue;
    private File mixpanelBaseFolder;

    public MixpanelDataManager() throws IOException {
        TolerantQueue.Converter<MixpanelTask> converter = new GsonConverter<MixpanelTask>(
                new Gson(), MixpanelTask.class);
        mixpanelTaskQueue = new MixpanelTaskQueue(
                new TolerantQueue<MixpanelTask>(new File(Configurations.getMixpanelQueueFile()), converter));
        mixpanelBaseFolder = new File(Configurations.getMixpanelDataBase());
        mixpanelBaseFolder.mkdirs();
        if (!mixpanelBaseFolder.canWrite()) {
            throw new IOException("mixpanelData folder error");
        }
    }

    @Override
    public MixpanelTaskQueue getPostDataQueue() {
        return mixpanelTaskQueue;
    }

    @Override
    public void sendData(String appId, String target, String content) {
        if (TextUtils.isEmpty(content)) {
            Log.i(TAG,"POST CONTENT IS EMPTY");
            return;
        }
        mixpanelTaskQueue.add(new MixpanelTask(target, content));

        //String username = TurtleManagers.userManager.user.username;
        /*String username = "gynsolomon"; //for test
        if(username == null) {
            Toast.makeText(TurtleApplication.getAppContext(), "Please Login!", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Not Login");
            return;
        }

        File distinctMixpanelFolder = new File(mixpanelBaseFolder, username);
        if(!(distinctMixpanelFolder.exists() && distinctMixpanelFolder.isDirectory())) {
            distinctMixpanelFolder.mkdirs();
        }

        try {
            FileUtils.writeStringToFile(new File(distinctMixpanelFolder,"data_points"), content);
            mixpanelTaskQueue.add(new MixpanelTask(target, content));
        } catch (IOException e) {
            Log.e(TAG,"write mixpanel data failed, the target is ==>" + target + ",content is ==>" + content);
        }*/
    }

    @Override
    public String getData(String appId, String entityId) {
        return null;
    }

    @Override
    public Map<String, String> getAll(String appId) {
        return null;
    }

    @Override
    public int deleteAll(String appId) {
        return 0;
    }

    @Override
    public String getUserInfo(String appId, String entityId) {
        return null;
    }
}

