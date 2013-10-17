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
public interface AppManager {

    public Collection<WebApp> getAllApps();

    public Map<String, WebApp> getAppsMap();

    public void removeAllApps();

    public void refresh();

    public boolean containsApp(String id);

    public WebApp getApp(String id);

    public List<WebApp> getApps(List<WebAppQuery> queries);

    public List<WebApp> getApps(String key, String value);

    public WebApp installApp(Context context, File appFile) throws WebAppException;

    public void uninstallApp(Context context, String id) throws WebAppException;

}
