package org.sunshinelibrary.turtle.userdatamanager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;

import org.apache.http.protocol.HTTP;

import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.postdata.PostDataTask;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 14-3-10
 * Time: PM12:01
 */
public class UserDataTask extends PostDataTask {

    private String mHttpMethod;

    public UserDataTask(String target, String content,String httpMethod) {
        super(target,content);
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

        if(this.content!=null){
            StringEntity postEntity = new StringEntity(this.content);
            postEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

            HttpEntityEnclosingRequestBase request = null;

            if(mHttpMethod.equals("POST")){
                request = new HttpPost(url);
            }else if (mHttpMethod.equals("PUT")){
                request = new HttpPut(url);
            }

            if(request!=null){
                request.setEntity(postEntity);
                HttpResponse response = client.execute(request, context);
                Logger.i("----------->USERDATA POST CODE => "+response.getStatusLine().getStatusCode());
                if (response.getStatusLine().getStatusCode()>=300 || response.getStatusLine().getStatusCode()<200) {
                    Logger.e("send userdata failed, wait for next sync");
                    return;
                }
                ((UserDataTaskQueue) TurtleManagers.userDataManager.getPostDataQueue()).remove();
            }else{
                Logger.e("Not generate Http Request instance");
            }
        }else{
            Logger.e("there's no content in this post");
            ((UserDataTaskQueue) TurtleManagers.userDataManager.getPostDataQueue()).remove();
        }
    }

}
