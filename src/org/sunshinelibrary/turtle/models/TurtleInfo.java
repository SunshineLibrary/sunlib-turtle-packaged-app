package org.sunshinelibrary.turtle.models;

/**
 * User: fxp
 * Date: 10/18/13
 * Time: 3:35 PM
 */
public class TurtleInfo {

    public String localHost;
    public String apiHost;
    public UserInfo userInfo;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("服务器地址:");
        sb.append(apiHost);
        sb.append("\r\n");
        sb.append("用户信息:");
        sb.append("\r\n");
        sb.append(userInfo);
        return sb.toString();
    }

    public static class UserInfo {
        public String userName;
        public String accessToken;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("UserName:");
            sb.append(userName);
            sb.append("\r\n");
            sb.append("AccessToken:");
            sb.append("\r\n");
            sb.append(accessToken);
            return sb.toString();
        }
    }
}
