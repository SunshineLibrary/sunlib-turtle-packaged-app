package org.sunshinelibrary.turtle.appmanager;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.utils.WebAppParser;
import org.sunshinelibrary.turtle.webservice.RestletWebService;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 3:30 PM
 */
public class WebAppManager implements AppManager {

    Map<String, WebApp> apps = new ConcurrentHashMap<String, WebApp>();

    @Override
    public String getAppsDir() {
        return "/sdcard/webapps/";
    }

    @Override
    public Collection<WebApp> getAllApps() {
        return apps.values();
    }

    @Override
    public List<WebApp> getApps(List<WebAppQuery> queries) {
        return null;
    }

    @Override
    public List<WebApp> getApps(String key, String value) {
        return null;
    }

    @Override
    public WebApp installApp(Context context, File appFile) throws WebAppException {
        Logger.i("install app start," + appFile);

        WebApp newApp = null;
        // Generate this app
        try {
            newApp = WebAppParser.parse(appFile);
            // TODO unzip this zip to apps folder

            apps.put(newApp.id, newApp);
            // Notify server to refresh app
        } catch (Exception e) {
            e.printStackTrace();
        }

        SystemClock.sleep(3000);
        Logger.i("install app complete," + appFile);
        return newApp;
    }

    @Override
    public void uninstallApp(Context context, String id) throws WebAppException {
        // Notify server to refresh app
        if (apps.containsKey(id)) {
            apps.remove(id);
            Logger.i("remove app," + id);
            Intent intent = new Intent(context, RestletWebService.class);
            intent.setAction("uninstall app");
            context.startActivity(intent);
        } else {
            Logger.i("app not exists," + id);
        }
    }
}
