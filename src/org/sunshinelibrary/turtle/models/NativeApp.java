package org.sunshinelibrary.turtle.models;

/**
 * User: fxp
 * Date: 11/8/13
 * Time: 8:08 PM
 */
public class NativeApp {

    public String packageName;
    public int versionCode;

    public NativeApp(String packageName, int versionCode) {
        this.packageName = packageName;
        this.versionCode = versionCode;
    }
}
