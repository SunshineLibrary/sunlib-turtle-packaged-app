package org.sunshinelibrary.turtle.user;

import org.sunshinelibrary.turtle.taskmanager.LoginTask;

/**
 * Created by hellmagic on 14-2-27.
 */
public class UserManager {
    public User user;
    public boolean isGetingUser;

    public void login(){
        new LoginTask().execute();
    }

}
