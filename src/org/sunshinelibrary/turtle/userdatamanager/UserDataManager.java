package org.sunshinelibrary.turtle.userdatamanager;

import java.util.Map;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 11:48 AM
 */
public interface UserDataManager {

    public void sendData(String id, String content);

    public String getData(String id);

    public Map<String, String> getAll();
}
