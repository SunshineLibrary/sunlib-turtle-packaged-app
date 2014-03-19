package org.sunshinelibrary.turtle.postdata;

import com.squareup.tape.Task;
import org.sunshinelibrary.turtle.utils.Logger;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 14-3-6
 * Time: PM12:55
 */
public abstract class PostDataTask implements Serializable, Task<String> {
    public String target;
    public String content;

    public PostDataTask(String target, String content) {
        this.target = target;
        this.content = content;
    }

    @Override
    public void execute(String msg) {
        try {
            upload();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(msg+"=====>upload post-data failed");
        }
    }

    protected abstract void upload() throws Exception;
}
