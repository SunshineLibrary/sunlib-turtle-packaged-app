package org.sunshinelibrary.turtle.utils;

import org.sunshinelibrary.turtle.models.WebApp;

import java.util.ArrayList;
import java.util.List;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 1:58 PM
 */
public class Diff {

    public static DiffManifest generateDiffTask(List<WebApp> localApps, List<WebApp> remoteApps) {
        // TODO Calculate different parts
        DiffManifest manifest = new DiffManifest();
        manifest.newApps = new ArrayList<WebApp>();
//        manifest.newApps.add(new WebApp());
//        manifest.newApps.add(new WebApp());
//        manifest.newApps.add(new WebApp());
        manifest.deletedApps = new ArrayList<WebApp>();
//        manifest.deletedApps.add(new WebApp());
//        manifest.deletedApps.add(new WebApp());
//        manifest.deletedApps.add(new WebApp());
        return manifest;
    }

}
