package org.sunshinelibrary.turtle.appmanager;

import android.content.Context;
import com.google.common.io.Files;
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
public class WebAppManager extends AppManager {

    Map<String, WebApp> apps = new ConcurrentHashMap<String, WebApp>();

    public WebAppManager(Context context) {
        super(context);
    }

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
    public WebApp getApp(String id) {
        return apps.get(id);
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
    public WebApp installApp( File appFile) throws WebAppException {
        // TODO Clean up all temp files
        WebApp newApp = null;
        File appFolder = null;
        try {
            Logger.i("install app," + appFile);
            newApp = WebAppParser.parse(appFile);
            if (newApp == null) {
                throw new WebAppException("app parser failed," + appFile.getAbsolutePath());
            }
            String appId = newApp.getId();
            appFolder = new File(Configurations.getAppBase(), newApp.getId());
            if (apps.containsKey(appId)) {
                WebApp localApp = apps.get(appId);
                int localVersion = localApp.getVersionCode();
                int remoteVersion = newApp.getVersionCode();
                if (localVersion >= remoteVersion) {
                    Logger.i("local app already exists, or newer than server app," + localVersion + "," + remoteVersion);
                    return localApp;
                } else {
                    Logger.i("update app," + appId + ",from " + localVersion + ",to " + remoteVersion);
                    /**
                     * Update app
                     * 1. unzip app to a temp dir
                     * 2. delete exists app folder
                     * 3. move temp dir to new app folder
                     */
                    File tmpFolder = Files.createTempDir();
                    ZipUtils.unzip(appFile, tmpFolder);
                    FileUtils.deleteDirectory(appFolder);
                    FileUtils.moveDirectory(tmpFolder, appFolder);
                    apps.put(newApp.getId(), newApp);
                }
            } else {
                FileUtils.deleteDirectory(appFolder);
                ZipUtils.unzip(appFile, appFolder);
                apps.put(newApp.getId(), newApp);
            }
            Logger.i("install app complete," + appFile);
        } catch (Exception e) {
            Logger.e("install app failed," + e.getMessage());
            if (appFolder != null) {
                try {
                    Logger.e("delete unzipped folder");
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
    public void uninstallApp(String id) throws WebAppException {
        // Notify server to refresh app
        if (apps.containsKey(id)) {
            Logger.i("uninstall app," + id);
            apps.remove(id);
            File appFolder = new File(Configurations.getAppBase(), id);
            try {
                FileUtils.deleteDirectory(appFolder);
            } catch (IOException e) {
                Logger.i("clean app folder failed," + appFolder.getAbsolutePath() + "," + e.getMessage());
            }
            Logger.i("uninstall app success," + id);
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
