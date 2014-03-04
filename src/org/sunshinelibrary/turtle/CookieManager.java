package org.sunshinelibrary.turtle;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

/**
 * Created by hellmagic on 14-3-2.
 */
public class CookieManager {
    public static DefaultHttpClient client = new DefaultHttpClient();
    public static BasicCookieStore cookieStore = new BasicCookieStore();
    public static BasicHttpContext httpContext = new BasicHttpContext();

}
