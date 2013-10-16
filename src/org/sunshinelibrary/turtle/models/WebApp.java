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

//    public String id;
//    public int version_code;
//    public String url;
//    public String home;
    // the whole manifest.json in app zip
    public JSONObject manifest;

    public WebApp(JSONObject manifest) {
        this.manifest = manifest;
    }

    public String getId() {
        String ret = null;
        try {
            ret = manifest.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ret;
    }
}
