package org.sunshinelibrary.turtle.utils;

import java.text.SimpleDateFormat;

/**
 * User: fxp
 * Date: 10/21/13
 * Time: 1:39 PM
 */
public class DateFormater {

    public static String format(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm:ss");
        return sdf.format(time);
    }

}
