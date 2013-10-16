package org.sunshinelibrary.turtle.userdatamanager;

import com.squareup.tape.ObjectQueue;
import com.squareup.tape.TaskQueue;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 11:54 AM
 */

public class UserDataTaskQueue extends TaskQueue<UserDataTask> {


    public UserDataTaskQueue(ObjectQueue<UserDataTask> delegate) {
        super(delegate);
    }
}