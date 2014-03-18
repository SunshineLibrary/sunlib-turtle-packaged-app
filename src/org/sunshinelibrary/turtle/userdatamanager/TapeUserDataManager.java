package org.sunshinelibrary.turtle.userdatamanager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.io.FileUtils;
import org.sunshinelibrary.turtle.TurtleApplication;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.GsonConverter;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.utils.TolerantQueue;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 12:09 PM
 */
public class TapeUserDataManager implements UserDataManager<UserDataTaskQueue> {

    public UserDataTaskQueue userDataQueue;
    private File userDataBaseFolder = null;
    private String accessToken = null;

    //TODO:the constructor function need to change

    public TapeUserDataManager(Context context) throws IOException {
//through the accessToken to get the username, then make the username folder to host all the user data belong to the user
        accessToken = Configurations.getAccessToken();
        //TODO:through the accessToken get the username, then create the username folder
        if (accessToken == null) {
            throw new IOException("access token is null");
        }

        TolerantQueue.Converter<UserDataTask> converter
                = new GsonConverter<UserDataTask>(new Gson(), UserDataTask.class);
        userDataQueue = new UserDataTaskQueue(new TolerantQueue<UserDataTask>(new File(Configurations.getUserDataQueueFile()), converter)
        );
        userDataBaseFolder = new File(Configurations.getUserDataBase());
        userDataBaseFolder.mkdirs();
        if (!userDataBaseFolder.canWrite()) {
            throw new IOException("userdata folder cannot write");
        }
    }

    public static String getEncodedId(String key) {//convert userId to Base32
        return (TextUtils.isEmpty(key)) ? null : new Base32().encodeAsString(key.getBytes());
    }

    @Override
    public UserDataTaskQueue getPostDataQueue() {
        return userDataQueue;
    }

    @Override
    public void sendData(String appId, String entityId, String content,String httpMethod) {
        //TODO:make the right folder path
        if(appId.trim().length() <= 0) {
           appId = Configurations.defaultPackage;
        }
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(entityId) || TextUtils.isEmpty(content)) {
            Logger.i("post cannot be empty");
            return;
        }
        String username = TurtleManagers.userManager.user.username;
        if(username == null) {
            Toast.makeText(TurtleApplication.getAppContext(), "Please Login!", Toast.LENGTH_LONG).show();
            Log.i("LiuCong", "Not Login");
            return;
        }
        File userdataAppFolder = new File(new File(userDataBaseFolder, username), appId);
        if(!(userdataAppFolder.exists() && userdataAppFolder.isDirectory())) {
            userdataAppFolder.mkdirs();
        }
        try {
            FileUtils.writeStringToFile(new File(userdataAppFolder, entityId), content);
            userDataQueue.add(new UserDataTask(appId+"/"+entityId, content, accessToken,httpMethod));  //send just send, not with accessToken
        } catch (IOException e) {
            Logger.e("write user data failed," + entityId + "," + content);
        }
    }

    @Override
    public String getData(String appId, String entityId) {
//this id just is entityId, but below use it as userId!!!this is not correct, the truth is entityId, because the real param is entityId
//TODO:implement the new user data save pattern /userdata/userId/appId/entityId  userinfo is a type of user data, the key is not entityId, just is "userInfo"
        String ret = "{}";
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(entityId)) {
            Logger.i("get userdata empty id,");
            return ret;
        }
        //String cacheID = getUserDataId(id);
        String username = TurtleManagers.userManager.user.username;
        if(username == null) {
            Toast.makeText(TurtleApplication.getAppContext(), "Please Login", 0).show();
            Log.i("LiuCong", "getData not login");
            return null;
        }

        if(appId.trim().length() <= 0) {
            appId = Configurations.defaultPackage;
        }

        File userdataFolder = new File(new File(userDataBaseFolder, username), appId);
        try {
            File dataFile = new File(userdataFolder, entityId);
            if (!dataFile.exists()) {
                Log.i("LiuCong", "userdata appId="+appId+"  entityId="+entityId+"  not exist");
            } else {
                ret = FileUtils.readFileToString(dataFile);
            }
        } catch (IOException e) {
            Logger.e("read user data failed," + entityId);
        }
        return ret;
    }

    @Override
    public Map<String, String> getAll(String appId) {
        String username = TurtleManagers.userManager.user.username;
        if(username == null) {
            Toast.makeText(TurtleApplication.getAppContext(), "Please Login", 0);
            Log.i("LiuCong", "getData not login");
            return null;
        }

        if(appId.trim().length() <= 0) {
            appId = Configurations.defaultPackage;
        }

        Map<String, String> ret = new HashMap<String, String>();
        if(username == null) {
            Toast.makeText(TurtleApplication.getAppContext(), "Please Login", 0);
            Log.i("LiuCong", "getAll no Login");
            return null;
        }
        File userdataAppFolder = new File(new File(userDataBaseFolder, username), appId);
        File[] files = userdataAppFolder.listFiles();
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
    public int deleteAll(String appId) {
        String username = TurtleManagers.userManager.user.username;
        if(username == null) {
            Toast.makeText(TurtleApplication.getAppContext(), "Please Login", 0);
            Log.i("LiuCong", "deleteAll not Login");
            return 0;
        }

        File userdataAppFolder = new File(new File(userDataBaseFolder, username), appId);
        File[] files = userdataAppFolder.listFiles();
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
            //TODO:if other task need to remove, attention!
        }
        Logger.i("all user data deleted");
        return 0;
    }

    @Override
    public String getUserInfo(String appId, String entityId) {
        Log.i("LiuCong", "TapUserdata: getUserInfo");
        return getData(appId, entityId);
    }
}
