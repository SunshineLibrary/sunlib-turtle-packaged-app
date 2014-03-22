package org.sunshinelibrary.turtle.taskmanager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.init.InitService;
import org.sunshinelibrary.turtle.syncservice.AppSyncService;
import org.sunshinelibrary.turtle.user.User;
import org.sunshinelibrary.turtle.utils.*;
import org.sunshinelibrary.turtle.webservice.RestletWebService;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 14-3-11
 * Time: AM11:30
 */

public class LoginTask extends AsyncTask<Void, Void, User>{

    private Context mContext;

    public LoginTask(Context context){
        this.mContext = context;
        Intent serverIntent = new Intent(mContext, RestletWebService.class);
        mContext.startService(serverIntent);
    }

    @Override
    protected void onPreExecute(){
        if(!Configurations.isOnline(mContext)){
            Logger.i("LoginTaskStart under offline network");
            if(TurtleManagers.userManager.user==null && !TurtleInfoUtils.getUserInfo(mContext).equals("")){
                TurtleManagers.userManager.user = new Gson().fromJson(TurtleInfoUtils.getUserInfo(mContext),User.class);
            }
            this.cancel(true);
            return;
        }
        InitService.isLoginTaskRunning = true;
        Logger.i("-=-=-=-=-=-=-=-=-=-==-=-=-=-=-=-=-=-> Turtle is doing login task!!!");
    }

    @Override
    protected User doInBackground(Void... voids) {
        User user = null;
        TurtleManagers.userManager.isGetingUser = true;
        String access_token = TurtleManagers.userManager.getAccessToken();
        if(!access_token.equals("") &&  access_token != null) {
            Logger.i("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-》Token: "+access_token);
            HttpClient client = TurtleManagers.cookieManager.client;
            BasicCookieStore cookieStore = TurtleManagers.cookieManager.cookieStore;
            BasicHttpContext context = TurtleManagers.cookieManager.httpContext;
            try {
                cookieStore.clear();  // This is for online auth, otherwise it will carry two cookie with the same name "connect.sid"
                RemoteCookie mCookie = new Gson().fromJson(access_token, RemoteCookie.class);
                BasicClientCookie myCookie = new BasicClientCookie(mCookie.name,mCookie.value);
                myCookie.setDomain(mCookie.cookieDomain);
                myCookie.setPath(mCookie.cookiePath);
                myCookie.setAttribute("path","/");
                myCookie.setSecure(mCookie.isSecure);
                myCookie.setVersion(mCookie.cookieVersion);
                cookieStore.addCookie(myCookie);   // this is for reboot machine, add the cookie to cookie store and gain auth.
                Logger.i("-=-=-=-=-=-=-=-=-=-=-=-=-=-=->SuccessAddTokenToCookie ==>"+new Gson().toJson(cookieStore.getCookies().get(0)));
                context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

                // 尝试在线向云端服务器登录获取用户信息， 以验证当前token 是否失效，如果失效，则删除所有用户信息。
                String url = Configurations.upstreamServer + "/me";
                HttpGet httpGet = new HttpGet(url);

                try {
                    HttpResponse httpResponse = client.execute(httpGet, context);
                    if(httpResponse.getStatusLine().getStatusCode() == 200) {
                        String userString = StreamToString.convertStreamToString(httpResponse.getEntity().getContent());
                        user = new Gson().fromJson(userString, User.class);
                        Logger.i("Turtle Login Task Succeed");
                    } else{
                        //验证未通过，当前 token 失效，清空所有 "tmp" & "file stored" UserInfo.
                        TurtleManagers.userManager.clearUser();
                        Logger.e("Turtle Login Task Failed, clear all user info, and the code is ==>"+httpResponse.getStatusLine().getStatusCode());
                    }
                } catch (IOException e) {
                    Logger.i("Server Error");
                    e.printStackTrace();
                    this.cancel(true);
                }
            }catch (RuntimeException e){
                e.printStackTrace();
                Logger.e("error occur when add cookies");
                this.cancel(true);
            }
        } else{
            Logger.i("-=-=-=-=-=-=-=-=-=-=-=>Cannot find accesstoken!");
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
            Intent syncIntent = new Intent(mContext, AppSyncService.class);
            mContext.startService(syncIntent);
        }
        InitService.isLoginTaskRunning = false;
    }
}