package org.sunshinelibrary.turtle.user;

import android.content.Context;
import org.sunshinelibrary.turtle.TurtleApplication;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.init.InitService;
import org.sunshinelibrary.turtle.taskmanager.LoginTask;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.TurtleInfoUtils;


/**
 * Created by hellmagic on 14-2-27.
 */
public class UserManager {
    public User user;
    public boolean isGetingUser;
    private Context mContext;

    public UserManager(Context context){
        this.mContext = context;
    }

    public void login(){
        if(!InitService.isLoginTaskRunning && Configurations.isOnline(mContext)){
            new LoginTask().execute();
        }
    }

    public String getAccessToken(){
        return TurtleInfoUtils.getAccessToken(mContext);
    }

    public void clearUser(){
        TurtleInfoUtils.destroyInfo(mContext);                    //shared_prefs
        TurtleManagers.cookieManager.cookieStore.clear();         //cookie
        TurtleManagers.userManager.user = null;                   //memory user
    }
}
