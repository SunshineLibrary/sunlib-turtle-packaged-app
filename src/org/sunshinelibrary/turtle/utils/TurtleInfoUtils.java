package org.sunshinelibrary.turtle.utils;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;
import org.apache.http.conn.util.InetAddressUtils;
import org.restlet.Context;
import org.sunshinelibrary.turtle.models.TurtleInfo;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * User: fxp
 * Date: 10/18/13
 * Time: 3:34 PM
 */
public class TurtleInfoUtils {

    public static TurtleInfo getTurtleInfo(Context context) {
        TurtleInfo ret = null;
        try {
            TurtleInfo info = new TurtleInfo();
            info.localHost = getIPAddress(true);
            info.apiHost = Configurations.serverHost;
            info.userInfo = getUserInfo(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static TurtleInfo.UserInfo getUserInfo(Context context) {
//        Context mContext = getApplicationContext().createPackageContext("org.sunshinelibrary.login", Context.MODE_MULTI_PROCESS);
//        SharedPreferences preferences = mContext.getSharedPreferences("LOGIN",Context.MODE_MULTI_PROCESS);
        return null;
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static ConnectionState getLocalServerState() {
        String localServer = Configurations.serverHost;

        ConnectionState ret = new ConnectionState(
                localServer,
                Calendar.getInstance().getTimeInMillis(),
                false,
                0);
        try {
            URL url = new URL(ret.targetAPI);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try {
                InputStream in = new BufferedInputStream(connection.getInputStream());
                byte[] buf = new byte[4096];
                int count = -1;
                while ((count = in.read(buf)) != -1) {
                }
                ret.isConnected = true;
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            ret.isConnected = false;
        }
        long end = Calendar.getInstance().getTimeInMillis();
        ret.connectionDelay = end - ret.checkTime;
        return ret;
    }

    public static String getAccessToken(android.content.Context context) {
        String access_token = null;
        try {
            android.content.Context mContext = context.createPackageContext("org.sunshinelibrary.login", android.content.Context.MODE_MULTI_PROCESS);
            SharedPreferences preferences = mContext.getSharedPreferences("LOGIN", android.content.Context.MODE_MULTI_PROCESS);
            access_token = preferences.getString("ACCESS_TOKEN", "");
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "无法获得用户AccessToken，将使用临时AccessToken", Toast.LENGTH_LONG).show();
            Logger.e("cannot get access token, use test instead");
            access_token = "test";
        }
        return access_token;
    }

}
