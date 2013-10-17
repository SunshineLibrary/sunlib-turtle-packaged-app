package org.sunshinelibrary.turtle.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 7:55 PM
 */
public class WebApp {

//    public String localFolder;
    /**
     * TODO Delete all unnecessary fields
     */

    public String id;
    public int version_code;
    //    public String url;
//    public String home;
    // the whole manifest.json in app zip
    public String download_url;
    public JSONObject manifest;

    public WebApp(JSONObject manifest) throws JSONException {
        this.manifest = manifest;
        this.id = manifest.getString("id");
        this.version_code = manifest.getInt("version_code");
    }

    public int getVersionCode() {
        int ret = -1;
        try {
            if (manifest != null) {
                ret = manifest.getInt("version_code");
            } else {
                ret = version_code;
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ret;
    }

    public String getId() {
        String ret = null;
        try {
            if (manifest != null) {
                ret = manifest.getString("id");
            } else {
                ret = id;
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ret;
    }

    private String getUniqId() {
        return id + "." + version_code;
    }

    @Override
    public int hashCode() {
        return getUniqId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (!o.getClass().equals(WebApp.class)) && (o.hashCode() == this.hashCode());
    }

    @Override
    public String toString() {
        return getUniqId();
    }
}
