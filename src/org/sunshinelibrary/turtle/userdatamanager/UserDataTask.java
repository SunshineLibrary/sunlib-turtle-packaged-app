package org.sunshinelibrary.turtle.userdatamanager;

import com.squareup.tape.Task;
import org.sunshinelibrary.turtle.postdata.PostDataTask;

import java.io.Serializable;
import java.net.URL;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 11:51 AM
 */
public class UserDataTask extends PostDataTask {

    public UserDataTask(String target, String content, String accessToken) {
        super(target,content,accessToken);
    }

    @Override
    protected void upload() throws Exception{

    }
}
