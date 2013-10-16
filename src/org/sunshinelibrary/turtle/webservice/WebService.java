package org.sunshinelibrary.turtle.webservice;

import org.sunshinelibrary.turtle.models.WebApp;

import java.util.List;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 8:37 PM
 */
public interface WebService {

    public void attachApp(WebApp app);

    public void detachApp(String id);

    public List<WebApp> getAllApps();

}
