package org.sunshinelibrary.turtle.mixpanel;

import com.squareup.tape.ObjectQueue;
import com.squareup.tape.TaskQueue;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 14-3-6
 * Time: PM2:43
 */
public class MixpanelTaskQueue extends TaskQueue<MixpanelTask> {
    public MixpanelTaskQueue(ObjectQueue<MixpanelTask> delegate) {
        super(delegate);
    }
}
