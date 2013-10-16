package org.sunshinelibrary.turtle.syncservice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import org.sunshinelibrary.turtle.SyncTriggerReceiver;

import java.util.Map;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 8:59 PM
 */
public enum SyncEvent {
    SYNC_START("org.sunlib.turtle.action.DO_SYNC"),
    SYNC_COMPLETE("org.sunlib.turtle.action.COMPLETE_SYNC"),
    SYNC_EXCEPTION(""),
    NEW_APP(""),
    DELETE_APP("");
    public Map<String, Object> params;
    public String actionName;

    SyncEvent(String actionName) {
        this.actionName = actionName;
    }

    public PendingIntent createBroadcast(Context context) {
        Intent intent = new Intent(context, SyncTriggerReceiver.class);
        intent.setAction(actionName);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
