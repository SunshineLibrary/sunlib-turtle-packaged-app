package org.sunshinelibrary.turtle.utils;

import android.util.Log;

/**
 * User: fxp
 * Date: 10/15/13
 * Time: 2:07 PM
 */
public class Logger {

    public static final String TAG = "turtle";

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void v(String message) {
        Log.v(TAG, message);
    }

}
