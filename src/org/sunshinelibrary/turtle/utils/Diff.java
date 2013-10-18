package org.sunshinelibrary.turtle.utils;

import org.sunshinelibrary.turtle.models.WebApp;

import java.util.Map;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 1:58 PM
 */
public class Diff {

    public static DiffManifest generateDiffTask(Map<String, WebApp> localApps, Map<String, WebApp> remoteApps) {
        DiffManifest manifest = new DiffManifest();
        for (WebApp localApp : localApps.values()) {
            WebApp remoteApp = remoteApps.get(localApp.getId());
            if (remoteApp == null) {
                manifest.deletedApps.add(localApp);
            } else {
                if (localApp.getVersionCode() < remoteApp.getVersionCode()) {
                    manifest.newApps.add(remoteApp);
                }
                remoteApps.remove(remoteApp.getId());
            }
        }
        manifest.newApps.addAll(remoteApps.values());
        return manifest;
    }

}
