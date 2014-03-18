package org.sunshinelibrary.turtle.userdatamanager;

import com.google.gson.JsonObject;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONObject;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.postdata.PostDataTask;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;

import java.io.*;
import java.net.URL;

/**
 * User: fxp
 * Date: 10/16/13
 * Time: 11:51 AM
 */
public class UserDataTask extends PostDataTask {

    private String mHttpMethod;

    public UserDataTask(String target, String content, String accessToken,String httpMethod) {
        super(target,content,accessToken);
        this.mHttpMethod = httpMethod;

    }

    @Override
    protected void upload() throws Exception{
        String url = Configurations.getSunlibAPI(Configurations.SunAPI.USERDATA) + this.target;
        Logger.i("----------------->UserDATA URL:"+url);
        HttpClient client = TurtleManagers.cookieManager.client;
        BasicHttpContext context = TurtleManagers.cookieManager.httpContext;
        BasicCookieStore cookieStore = TurtleManagers.cookieManager.cookieStore;
        if(cookieStore.getCookies().isEmpty()) {
            Logger.e("There's something wrong, cookie not found");
            return;
        }
        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        InputStream input = new StringBufferInputStream(new JSONObject(this.content).toString());
        HttpEntity postEntity = new InputStreamEntity(input, 8192);

        HttpEntityEnclosingRequestBase request = null;

        if(mHttpMethod.equals("POST")){
            request = new HttpPost(url);
        }else if (mHttpMethod.equals("PUT")){
            request = new HttpPut(url);
        }

        if(request!=null){
            request.setHeader("Content-Type", "application/json;charset:UTF-8");
            request.setEntity(postEntity);
            HttpResponse response = client.execute(request, context);

            if (response.getStatusLine().getStatusCode()>=300 || response.getStatusLine().getStatusCode()<200) {
                Logger.e("send userdata failed, wait for next sync");
                return;
            }
            ((UserDataTaskQueue) TurtleManagers.userDataManager.getPostDataQueue()).remove();
        }else{
            Logger.e("Not generate Http Request instance");
        }


    }

}
