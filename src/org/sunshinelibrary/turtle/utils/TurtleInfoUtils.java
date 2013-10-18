package org.sunshinelibrary.turtle.utils;

import org.apache.http.conn.util.InetAddressUtils;
import org.restlet.Context;
import org.sunshinelibrary.turtle.models.TurtleInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
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

}
