package org.sunshinelibrary.turtle.syncservice;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 8:55 PM
 */
public interface SyncService {

    public void doSync(SyncListener listener);

    public void register(SyncEvent event, SyncListener listener);

    public void unregister(SyncEvent event, SyncListener listener);

    public long getLastSyncTime();

    public long getLastSyncCompleteTime();
}
