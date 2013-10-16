package org.sunshinelibrary.turtle.utils;

import java.io.File;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 3:24 PM
 */
public class Configurations {

    public static final String storageBase = "/sdcard/webapps";
    public static String serverHost = "http://192.168.1.10:3000";

    public static void init() {
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
    }

    public static String getServerHost() {
        return serverHost;
    }

    public static String getSunlibAPI(SunAPI api) {
        if (SunAPI.APPSJSON.equals(api)) {
            return serverHost + "/apps.json";
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

    public static enum SunAPI {
        APPSJSON;
    }


}
