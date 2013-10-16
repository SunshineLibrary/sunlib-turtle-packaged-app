package org.sunshinelibrary.turtle.appmanager;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 5:57 PM
 */
public class WebAppException extends Exception {

    public WebAppException(String s) {
        super(s);
    }

    public WebAppException(Exception e) {
        super(e);
    }
}
