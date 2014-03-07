package org.sunshinelibrary.turtle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.sunshinelibrary.turtle.appmanager.AppManager;
import org.sunshinelibrary.turtle.appmanager.WebAppManager;
import org.sunshinelibrary.turtle.mixpanel.MixpanelDataManager;
import org.sunshinelibrary.turtle.taskmanager.SyncTaskManager;
import org.sunshinelibrary.turtle.taskmanager.TaskManager;
import org.sunshinelibrary.turtle.userdatamanager.TapeUserDataManager;
import org.sunshinelibrary.turtle.userdatamanager.UserDataManager;
import org.sunshinelibrary.turtle.usermanager.UserManager;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 3:27 PM
 */
public class TurtleManagers {
    public static AppManager appManager;
    public static TaskManager taskManager;
    public static UserDataManager userDataManager;
    public static UserManager userManager;
    public static CookieManager cookieManager;
    public static MixpanelDataManager mixpanelManager;
    public static boolean isInit = false;

    public static void init(Context context) throws Exception {
        SharedPreferences settings = context.getSharedPreferences(Configurations.TURTLE_SHARED_PREFERENCE, 0);
        if (!settings.getBoolean(Configurations.TURTLE_SHARED_PREFERENCE_INIT, false)) {
            // Delete all turtle folder exists
            //FileUtils.deleteDirectory(new File(Configurations.getStorageBase()));
            Logger.i("first start, clean all files in turtle folder");
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Configurations.TURTLE_SHARED_PREFERENCE_INIT, true);
            editor.commit();
        } else {
            Logger.i("turtle already installed, maybe upgrade");
        }

        Configurations.init(context);
        appManager = new WebAppManager(context);
        taskManager = new SyncTaskManager();
        userDataManager = new TapeUserDataManager(context);
        userManager = new UserManager();
        mixpanelManager = new MixpanelDataManager();
        isInit = true;
    }

    public static void loginTask() {
        new LoginTask().execute();
    }


    private static class LoginTask extends AsyncTask<Void, Void, User> {

        @Override
        protected User doInBackground(Void... voids) {
            User user = null;//Cookie
            TurtleManagers.userManager.isGetingUser = true;
            String access_token = Configurations.getAccessToken();
            if(access_token.equals("") || access_token.trim().equals("")) {
                return null;
            }

            DefaultHttpClient client = TurtleManagers.cookieManager.client;
            BasicCookieStore cookieStore = TurtleManagers.cookieManager.cookieStore;
            BasicHttpContext context = TurtleManagers.cookieManager.httpContext;

            List<Cookie> cookies = cookieStore.getCookies();
            if(cookies!=null){
                try {
                    cookies.add(new BasicClientCookie("session_id", access_token));
                }catch (RuntimeException e){
                    e.printStackTrace();
                    Logger.e("error occur when add cookies");
                }
            }

            String url = Configurations.upstreamServer + "/me";
            HttpGet httpGet = new HttpGet(url);

            try {
                HttpResponse httpResponse = client.execute(httpGet, context);
                if(httpResponse.getStatusLine().getStatusCode() == 200) {
                    String userString = httpResponse.getEntity().toString();
                    user = new Gson().fromJson(userString, User.class);
                } else {
                    Log.i("Turtle", "Server Response is not 200, but is " + httpResponse.getStatusLine().getStatusCode());
                }
            } catch (IOException e) {
                Log.i("Turtle", "Server Error");
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            TurtleManagers.userManager.isGetingUser = false;
            TurtleManagers.userManager.user = user;
            if(user != null) {
                File userdataFolder = new File(Configurations.getUserDataBase(), user.username);
                if(!userdataFolder.exists()) {
                    userdataFolder.mkdirs();
                }
            }
        }
    }
}
