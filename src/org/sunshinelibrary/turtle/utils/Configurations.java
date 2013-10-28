package org.sunshinelibrary.turtle.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 3:24 PM
 */
public class Configurations {

    public static Context mainActivity ;

    public static final String TURTLE_SHARED_PREFERENCE = "Turtle";
    public static final String TURTLE_SHARED_PREFERENCE_INIT = "init";
    public static final String LAUNCHER_APP_FILE = "0.zip";
    public static final String storageBase = "/sdcard/turtle";
    public static final int SYNC_INTERVAL = 30 * 1000;
            public static String serverHost = "http://192.168.3.12:3000";
//    public static String serverHost = "http://192.168.3.26:3000";
    //    public static String serverHost = "http://192.168.3.100";
    //    public static String serverHost = "http://shuwu.sunshine-library.org";
    public static String LOCAL_SERVER_HOST = "http://192.168.3.100";
    public static int localPort = 9460;
    public static String localHost = "http://127.0.0.1:" + localPort;
    public static String accessToken;
    public static long lastSync;
    public static long lastSuccessSync;
    private static Context context;

    public static boolean isInLocalNetwork() {
        return LOCAL_SERVER_HOST.equals(serverHost);
    }

    public static String convertLocalUrlToInternetUrl(String url) {
        return url.replace(LOCAL_SERVER_HOST, serverHost);
    }

    public static void init(Context context) {
        Configurations.context = context;
        boolean success = false;
        Logger.i("init all folders," + storageBase);
        success = new File(getStorageBase()).mkdirs();
        Logger.i("create storage base," + success);
        success = new File(getAppBase()).mkdirs();
        Logger.i("create app base," + success);
        success = new File(getMetaBase()).mkdirs();
        Logger.i("create meta base," + success);
        success = new File(getUserDataBase()).mkdirs();
        Logger.i("create userdata base," + success);
        accessToken = TurtleInfoUtils.getAccessToken(context);
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static String getServerHost() {
        return serverHost;
    }

    public static String getSunlibAPI(SunAPI api) {
        if (SunAPI.APPSJSON.equals(api)) {
//            return serverHost + "/apps.json";
            return serverHost + "/exercise/v1/apps.json";
        } else if (SunAPI.USERDATA.equals(api)) {
            return serverHost;
        }
        return null;
    }

    public static String getStorageBase() {
        return storageBase;
    }

    public static String getAppBase() {
        return getStorageBase() + "/app";
    }

    public static String getMetaBase() {
        return getStorageBase() + "/meta";
    }

    public static String getUserDataQueueFile() {
        return getMetaBase() + "/userdata.queue";
    }

    public static String getUserDataBase() {
        return getStorageBase() + "/userdata";
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static enum SunAPI {
        APPSJSON, USERDATA;
    }


}
