package org.sunshinelibrary.turtle.models;

import com.google.gson.JsonObject;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 7:55 PM
 */
public class WebApp {

    public String localFolder;
    /**
     * TODO Delete all unnecessary fields
     */

    public String id;
    public int version_code;
    public String url;
    public String home;
    // the whole manifest.json in app zip
    public JsonObject manifest;
}
