package org.sunshinelibrary.turtle.appmanager;

import android.content.Context;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.utils.WebAppParser;
import org.sunshinelibrary.turtle.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
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
    public Map<String, WebApp> getAppsMap() {
        return apps;
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
        File appFolder = null;
        try {
            newApp = WebAppParser.parse(appFile);
            String appId = newApp.getId();
            if (apps.containsKey(appId)) {
                Logger.i("app already exists," + appId);
                return apps.get(appId);
            }
            if (apps.get(appId).getVersionCode() > newApp.getVersionCode()) {
                Logger.i("local app is newer than new app," + appFile.getAbsolutePath());
                return apps.get(appId);
            }

            appFolder = new File(Configurations.getAppBase(), newApp.getId());
            FileUtils.deleteDirectory(appFolder);
            appFolder.mkdir();
            ZipUtils.unzip(appFile, appFolder);

            apps.put(newApp.getId(), newApp);
            Logger.i("install app complete," + appFile);
        } catch (Exception e) {
            e.printStackTrace();
            if (appFolder != null) {
                try {
                    Logger.e("install file failed, delete unzipped folder");
                    FileUtils.deleteDirectory(appFolder);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            throw new WebAppException("install app failed," + e.getMessage());
        }

        return newApp;
    }

    @Override
    public void uninstallApp(Context context, String id) throws WebAppException {
        // Notify server to refresh app
        if (apps.containsKey(id)) {
            // TODO Delete all apps
            apps.remove(id);
            Logger.i("remove app," + id);
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
