package org.sunshinelibrary.turtle.utils;

import android.text.TextUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.sunshinelibrary.turtle.appmanager.WebAppException;
import org.sunshinelibrary.turtle.models.WebApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 5:47 PM
 */
public class WebAppParser {

    public static WebApp parse(String filePath) throws WebAppException {
        return parse(filePath);
    }

    public static WebApp parse(File file) throws WebAppException {
        WebApp ret = null;
        try {
            ret = WebAppParser.parse(new ZipInputStream(new FileInputStream(file)));
        } catch (Exception e) {
            throw new WebAppException(e);
        }
        return ret;
    }

    public static WebApp parse(ZipInputStream zis) throws WebAppException {
        if (zis == null) {
            throw new WebAppException("zip input stream is null");
        }
        WebApp ret = null;
        ZipEntry entry;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                if ("manifest.json".equals(entryName)) {
                    String manifest = IOUtils.toString(zis, "UTF8");
                    ret = new WebApp(new JSONObject(manifest));
                    if (TextUtils.isEmpty(ret.getId())) {
                        throw new WebAppException("read app manifest failed, app id is null");
                    }
                    break;
                }
            }
            zis.close();
        } catch (Exception e) {
            try {
                zis.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new WebAppException(e);
        }
        return ret;
    }

}
