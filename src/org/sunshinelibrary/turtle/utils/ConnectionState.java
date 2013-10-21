package org.sunshinelibrary.turtle.utils;

/**
 * User: fxp
 * Date: 10/21/13
 * Time: 12:58 PM
 */
public class ConnectionState {

    public String targetAPI;
    public long checkTime;
    public boolean isConnected;
    public long connectionDelay;

    public ConnectionState(String testServer, long checkTime, boolean connected, long connectionDelay) {
        this.targetAPI = testServer;
        this.checkTime = checkTime;
        isConnected = connected;
        this.connectionDelay = connectionDelay;
    }

    @Override
    public String toString() {
        return "测试地址: " + targetAPI + "\r\n" +
                "更新时间: " + DateFormater.format(checkTime) + "\r\n" +
                (isConnected ? "连接正常，延迟 " + connectionDelay + " ms" : "连接服务器失败");
    }
}
