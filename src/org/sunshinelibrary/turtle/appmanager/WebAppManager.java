package org.sunshinelibrary.turtle.appmanager;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.utils.Configurations;
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
    public Collection<WebApp> getAllApps() {
        return apps.values();
    }

    @Override
    public void removeAllApps() {
        apps.clear();
    }

    @Override
    public boolean containsApp(String id) {
        return apps.containsKey(id);
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

            apps.put(newApp.getId(), newApp);
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

    public void refresh() {
        String appFolderPath = Configurations.getAppBase();
        File[] appFolders = new File(appFolderPath).listFiles();
        if (appFolders == null) {
            Logger.i("no app in app folder," + appFolderPath);
            return;
        }
        for (File appFolder : appFolders) {
            if (!appFolder.isDirectory()) {
                continue;
            }
            try {
                String manifest = FileUtils.readFileToString(new File(appFolder, "manifest.json"));
                JSONObject manifestObj = new JSONObject(manifest);
                Logger.i("app loaded," + manifestObj.toString());
                apps.put(manifestObj.getString("id"), new WebApp(manifestObj));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
