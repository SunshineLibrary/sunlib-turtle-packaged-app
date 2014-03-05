package org.sunshinelibrary.turtle.userdatamanager;

import java.util.Map;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 11:48 AM
 */
public interface UserDataManager {

    public UserDataTaskQueue getUserDataQueue();

    public void sendData(String appId, String entityId, String content);

    public String getData(String appId, String entityId);

    public Map<String, String> getAll(String appId);

    public int deleteAll(String appId);

    public String getUserInfo(String appId, String entityId);
}
