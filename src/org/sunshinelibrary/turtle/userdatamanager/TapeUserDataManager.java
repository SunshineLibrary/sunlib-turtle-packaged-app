package org.sunshinelibrary.turtle.userdatamanager;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.io.FileUtils;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.GsonConverter;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.utils.TolerantQueue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 12:09 PM
 */
public class TapeUserDataManager implements UserDataManager {

    public UserDataTaskQueue userDataQueue;
    private File userDataFolder = null;
    private String accessToken = null;

    public TapeUserDataManager(Context context) throws IOException {
        accessToken = Configurations.getAccessToken();
        if (accessToken == null) {
            throw new IOException("access token is null");
        }
        TolerantQueue.Converter<UserDataTask> converter
                = new GsonConverter<UserDataTask>(new Gson(), UserDataTask.class);
        userDataQueue = new UserDataTaskQueue(
                new TolerantQueue<UserDataTask>(
                        new File(Configurations.getUserDataQueueFile()), converter)
        );
        userDataFolder = new File(Configurations.getUserDataBase());
        userDataFolder.mkdirs();
        if (!userDataFolder.canWrite()) {
            throw new IOException("userdata folder cannot write");
        }
    }

    public static String getUserDataId(String key) {
        return (TextUtils.isEmpty(key)) ? null : new Base32().encodeAsString(key.getBytes());
    }

    @Override
    public UserDataTaskQueue getUserDataQueue() {
        return userDataQueue;
    }

    @Override
    public void sendData(String id, String content) {
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(content)) {
            Logger.i("post userdata cannot be empty," + id + "," + content);
            return;
        }
        String cacheID = getUserDataId(id);
        Logger.i("cached," + cacheID + "," + content);
        try {
            FileUtils.writeStringToFile(
                    new File(userDataFolder, cacheID),
                    content
            );
            userDataQueue.add(new UserDataTask(id, content, accessToken));
        } catch (IOException e) {
            Logger.e("write user data failed," + cacheID + "," + content);
        }
    }

    @Override
    public String getData(String id) {
        String ret = "{}";
        if (TextUtils.isEmpty(id)) {
            Logger.i("get userdata empty id," + id);
            return ret;
        }
        String cacheID = getUserDataId(id);
        try {
            File dataFile = new File(userDataFolder, cacheID);
            if (!dataFile.exists()) {
                Logger.v("user data not exists");
            } else {
                ret = FileUtils.readFileToString(dataFile);
            }
        } catch (IOException e) {
            Logger.e("read user data failed," + id);
        }
        return ret;
    }

    @Override
    public Map<String, String> getAll() {
        Map<String, String> ret = new HashMap<String, String>();
        File[] files = userDataFolder.listFiles();
        for (File file : files) {
            try {
                String key = new String(new Base32().decode(file.getName().getBytes()));
                ret.put(key, FileUtils.readFileToString(file));
            } catch (IOException e) {
                e.printStackTrace();
                Logger.e("read userdata file failed");
            }
        }
        return ret;
    }

    @Override
    public int deleteAll() {
        File[] files = userDataFolder.listFiles();
        Logger.i("delete all user data in local files");
        for (File file : files) {
            try {
                FileUtils.forceDelete(file);
                Logger.i("delete user data," + file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Logger.i("delete all user data in queue");
        while (true) {
            UserDataTask task = userDataQueue.peek();
            if (task == null) {
                break;
            }
            userDataQueue.remove();
        }
        Logger.i("all user data deleted");
        return 0;
    }
}
