package org.sunshinelibrary.turtle.appmanager;

import android.content.Context;
import org.sunshinelibrary.turtle.models.WebApp;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 7:54 PM
 */
public abstract class AppManager {

    protected Context context;

    protected AppManager(Context context) {
        this.context = context;
    }

    public abstract Collection<WebApp> getAllApps();

    public abstract Map<String, WebApp> getAppsMap();

    public abstract void removeAllApps();

    public abstract void refresh();

    public abstract boolean containsApp(String id);

    public abstract WebApp getApp(String id);

    public abstract List<WebApp> getApps(List<WebAppQuery> queries);

    public abstract List<WebApp> getApps(String key, String value);

    public abstract WebApp installApp(File appFile) throws WebAppException;

    public abstract void uninstallApp(String id) throws WebAppException;

}
