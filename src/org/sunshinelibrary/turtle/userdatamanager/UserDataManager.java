package org.sunshinelibrary.turtle.userdatamanager;

import java.util.Map;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 11:48 AM
 */
public interface UserDataManager<T> {

    public T getPostDataQueue();

    /**
     * This method is used for sending data to turtle's file system and cache post data for future use.
     * @param appId
     * @param entityId
     * @param content
     */
    public void sendData(String appId, String entityId, String content);

    public String getData(String appId, String entityId);

    public Map<String, String> getAll(String appId);

    public int deleteAll(String appId);

    public String getUserInfo(String appId, String entityId);
}
