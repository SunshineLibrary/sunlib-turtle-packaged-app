package org.sunshinelibrary.turtle.webservice;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 5:57 PM
 */
public class WebServiceException extends Exception {

    public WebServiceException(String s) {
        super(s);
    }

    public WebServiceException(Exception e) {
        super(e);
    }
}
