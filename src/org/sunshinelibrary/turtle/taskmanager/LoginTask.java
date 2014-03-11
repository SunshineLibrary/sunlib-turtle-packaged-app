package org.sunshinelibrary.turtle.taskmanager;

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
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.user.User;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.utils.StreamToString;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 14-3-11
 * Time: AM11:30
 */

public class LoginTask extends AsyncTask<Void, Void, User>{

    @Override
    protected User doInBackground(Void... voids) {
        User user = null;
        TurtleManagers.userManager.isGetingUser = true;
        String access_token = Configurations.getAccessToken();
        if(!access_token.equals("") &&  access_token != null) {

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
                    String userString = StreamToString.convertStreamToString(httpResponse.getEntity().getContent());
                    Logger.i("userString====>"+userString);
                    user = new Gson().fromJson(userString, User.class);
                } else {
                    Log.i("Turtle", "Server Response is not 200, but is " + httpResponse.getStatusLine().getStatusCode());
                }
            } catch (IOException e) {
                Log.i("Turtle", "Server Error");
                e.printStackTrace();
            }
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