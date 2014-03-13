package org.sunshinelibrary.turtle.webservice;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.util.Predicate;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONObject;
import org.restlet.*;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.StringRepresentation;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.sunshinelibrary.turtle.R;
import org.sunshinelibrary.turtle.TurtleApplication;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.user.User;
import org.sunshinelibrary.turtle.appmanager.WebAppException;
import org.sunshinelibrary.turtle.models.NativeApp;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;
import org.sunshinelibrary.turtle.utils.StreamToString;
import org.sunshinelibrary.turtle.utils.TurtleInfoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;

/**
 * User: fxp
 * Date: 10/14/13
 * Time: 8:45 PM
 */
public class RestletWebService extends Service implements WebService {
    public static <T> Collection<T> filter(Collection<T> target, Predicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for (T element : target) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    @Override
    public void attachApp(WebApp app) {
    }

    @Override
    public void detachApp(String id) {
    }

    @Override
    public List<WebApp> getAllApps() {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // start server
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, Configurations.localPort);
        component.getClients().add(Protocol.FILE);
        component.getClients().add(Protocol.HTTP);

        final Router router = new Router();
        router.setDefaultMatchingMode(Template.MODE_STARTS_WITH);
        router.setRoutingMode(Router.MODE_FIRST_MATCH);

        router.attachDefault(new Restlet() {
            @Override
            public void handle(Request request, Response response) {

                String requestPath = request.getResourceRef().getPath();
                if ("/".equals(requestPath)) {
                    if (TurtleManagers.userManager.user != null) {
                        response.redirectTemporary("/dispatch");
                    } else {
                        response.redirectTemporary("/webapp/login");
                    }
                    return;
                }

                try {
                    response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                    response.setEntity(new InputRepresentation(getAssets().open("404.html")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        router.attach("/dispatch", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                super.handle(request, response);
                User user = TurtleManagers.userManager.user;
                if (user != null) {
                    if (!"teacher".equals(user.usergroup)) {
                        response.redirectTemporary("/webapp/navigator");
//                        if (user.isProfileFullfill()) {
//                            response.redirectTemporary("/webapp/navigator");
//                        } else {
//                            response.redirectTemporary("/webapp/navigator");
//                        }
                    } else {
                        response.redirectTemporary("/webapp/me");
                    }
                } else {
                    response.redirectTemporary("/");
                }
            }
        });

        router.attach("/login", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                super.handle(request, response);
                String newRequestUrl = Configurations.upstreamServer + request.getResourceRef().getPath();
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                Form form = new Form(request.getEntity());
                Parameter parameter = form.get(0);
                User user = new Gson().fromJson(parameter.getName(), User.class);
                list.add(new BasicNameValuePair("username", user.username));
                list.add(new BasicNameValuePair("password", user.password));

                HttpClient client = TurtleManagers.cookieManager.client;
                BasicHttpContext context = TurtleManagers.cookieManager.httpContext;
                BasicCookieStore cookieStore = TurtleManagers.cookieManager.cookieStore;
                context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
                HttpPost post = new HttpPost(newRequestUrl);

                String httpResonpseResult = "";
                try {
                    post.setEntity(new UrlEncodedFormEntity(list));
                    HttpResponse httpResponse = client.execute(post, context);

                    if (httpResponse.getStatusLine().getStatusCode() >= 200 && httpResponse.getStatusLine().getStatusCode()<300) {
                        HttpEntity httpEntity = httpResponse.getEntity();
                        InputStream inputStream = httpEntity.getContent();
                        httpResonpseResult = StreamToString.convertStreamToString(inputStream);
                        User currentUser = new Gson().fromJson(httpResonpseResult, User.class);
                        TurtleManagers.userManager.user = currentUser;

                        List<Cookie> cookies = cookieStore.getCookies();
                        if (!cookies.isEmpty()) {
                            for (Cookie cookie : cookies) {
                                String cookieString = cookie.getName() + " : " + cookie.getValue();
                                if ("connect.sid".equals(cookie.getName())) {
                                    Logger.i("=-=-=-=-=-=-=-=-=-=-=->Token Written:"+ cookie.getValue());
                                    TurtleInfoUtils.writeAccessToken(TurtleApplication.getAppContext(), cookie.getValue());
                                }
                            }
                        }

                        if (TurtleManagers.userManager.user != null) {
                            File userFolder = new File(Configurations.getUserDataBase(), TurtleManagers.userManager.user.username);
                            if (!userFolder.exists()) {
                                userFolder.mkdirs();
                            }
                        }
                    } else {
                        Logger.e("online login failed!");
                        Logger.e(StreamToString.convertStreamToString(httpResponse.getEntity().getContent()));
                    }
                } catch (UnsupportedEncodingException e) {
                    Log.i("Turtle", "UnsupportedEncodingException");
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    Log.i("Turtle", "ClientProtocolException");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("Turtle", "IOException");
                    e.printStackTrace();
                }
                response.setEntity(new StringRepresentation(httpResonpseResult));
            }
        });

        router.attach("/me", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                super.handle(request, response);
                if(TurtleManagers.userManager.user!=null){
                    response.setStatus(Status.SUCCESS_OK);
                    response.setEntity(new JsonRepresentation(new Gson().toJson(TurtleManagers.userManager.user)));
                }else{
                    response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                    response.setEntity(new JsonRepresentation("{'message':'未登录'}"));
                }
            }
        });


        /**
         * Fake signout operation in turtle for it's no use to cache signout request.
         */
        router.attach("/signout", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                super.handle(request, response);
                Status status;
                String ret;

                if(!Configurations.isOnline(RestletWebService.this)){
                    Toast.makeText(RestletWebService.this,"无网络连接，无法进行该操作",Toast.LENGTH_LONG).show();
                    status = Status.CLIENT_ERROR_BAD_REQUEST;
                    ret = "无网络连接";
                }else{
                    status = Status.SUCCESS_OK;
                    ret = "已登出";
                    TurtleManagers.userManager.clearUser();
                    response.redirectTemporary("/");
                }
                response.setStatus(status);
                response.setEntity(new StringRepresentation(ret));
            }
        });

        router.attach("/apps", new
                Restlet() {
                    @Override
                    public void handle(Request request, Response response) {
                        String ret = null;
                        final Form queryForm = request.getResourceRef().getQueryAsForm();
                        final Set<String> filterKeys = queryForm.getNames();
                        final Map<String, List<String>> filterMap = new HashMap<String, List<String>>();
                        for (String key : filterKeys) {
                            String[] conditions = queryForm.getValuesArray(key);
                            filterMap.put(key, Arrays.asList(conditions));
                        }
                        Collection<WebApp> apps = TurtleManagers.appManager.getAllApps();
                        Predicate<WebApp> isFiltered = new Predicate<WebApp>() {
                            public boolean apply(WebApp app) {
                                boolean ret = true;
                                try {
                                    for (String key : filterKeys) {
                                        List<String> conditionsFilter = filterMap.get(key);
                                        if (!app.manifest.has(key) || !conditionsFilter.contains(app.manifest.getString(key))) {
                                            ret = false;
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    ret = false;
                                }
                                return ret;
                            }
                        };
                        List<JSONObject> result = new ArrayList<JSONObject>();
                        Collection<WebApp> filtered = filter(apps, isFiltered);
                        for (WebApp app : filtered) {
                            result.add(app.manifest);
                        }
                        response.setEntity(new StringRepresentation(result.toString()));
                    }
                });

        /**
        *   This is for Mixpanel data offline tracking.
        */
        router.attach("/tracks", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                final String TAG = "tracks";
                String ret;
                Status status;
                if (Method.POST.equals(request.getMethod())) {
                    try {
                        Representation representation = request.getEntity();
                        JSONObject jObject = new JSONObject(representation.getText());
                        Log.i(TAG, "type:" + representation.toString() + "====>" + jObject.toString());
                        TurtleManagers.mixpanelManager.sendData(null,"/tracks", jObject.toString());
                        status = Status.SUCCESS_OK;
                        ret = "success";
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "get body from request failed");
                        status = Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE;
                        ret = "Parse mixpanel json failed";
                    }
                } else {
                    status = Status.SERVER_ERROR_NOT_IMPLEMENTED;
                    ret = "api use error";
                    Log.e(TAG, "error use tracks api, should be http post");
                }
                response.setStatus(status);
                response.setEntity(new StringRepresentation(ret));
            }
        });



        router.attach("/userdata/me/info", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                super.handle(request, response);
                String userInfo = "";
                if (Method.GET.equals(request.getMethod())) {
                    userInfo = TurtleManagers.userDataManager.getUserInfo("me", "info");
                } else if (Method.POST.equals(request.getMethod())) {
                    try {
                        String content = request.getEntity().getText();
                        TurtleManagers.userDataManager.sendData("me", "info", content);
                        userInfo = content;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                response.setEntity(new StringRepresentation(userInfo));
            }
        });

        router.attach("/userdata/{appId}/{entityId}", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                super.handle(request, response);
                response.setEntity(new StringRepresentation(new User().toString()));
            }
        });
        router.attach("/reader", new ReaderRestlet());

        router.attach("/debug", new DebuggerRestlet());


        Application application = new SimpleApplication(router);
        // serve all app folder         //TODO:Host the static file, ex: lesson.json
        component.getDefaultHost().attach("/webapp/", new FileApplication("file://" + Configurations.getAppBase()));
        component.getDefaultHost().attach("", application);
        try {
            component.start();
            Logger.i("server started");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class FileApplication extends Application {
        public String rootUrl;

        public FileApplication(String rootUrl) {
            this.rootUrl = rootUrl;
        }

        @Override
        public Restlet createInboundRoot() {
            return new Directory(getContext(), rootUrl);
        }
    }

    public static class SimpleApplication extends Application {
        Router router;

        public SimpleApplication(Router router) {
            this.router = router;
        }

        public Restlet createInboundRoot() {
            router.setContext(getContext());
            return router;
        }
    }

    public static class FileRestletApplication extends Application {
        public String rootUrl;

        public FileRestletApplication(String rootUrl) {
            this.rootUrl = rootUrl;
        }

        @Override
        public Restlet createInboundRoot() {
            return new Directory(getContext(), rootUrl);
        }
    }

    public static class SimpleRestletApplication extends Application {
        Router router;

        public SimpleRestletApplication(Router router) {
            this.router = router;
        }

        public Restlet createInboundRoot() {
            router.setContext(getContext());
            return router;
        }
    }

    public class DebuggerRestlet extends Restlet {

        @Override
        public void handle(Request request, Response response) {
            DebugInfo ret = new DebugInfo();
            String requestPath = request.getResourceRef().getPath();
            if ("/debug/running_apps".equals(requestPath)) {
                ret.success = true;
                ret.content = getRunningApps();
            } else if ("/debug/userdata".equals(requestPath)) {
                ret.success = true;
                ret.content = getAllUserData();
            } else if ("/debug/access_token".equals(requestPath)) {
                ret.success = true;
                ret.content = getAccessToken();
            } else if ("/debug/install_app".equals(requestPath)) {
                try {
                    Form queryForm = request.getResourceRef().getQueryAsForm();
                    String downloadUrl = queryForm.getFirst("download_url").getValue();
                    URL url = new URL(downloadUrl);
                    WebApp app = installApp(url);
                    ret.success = true;
                    ret.content = new Gson().toJson(app);
                } catch (Exception e) {
                    ret.success = false;
                }
            } else if ("/debug/uninstall_app".equals(requestPath)) {
                Form queryForm = request.getResourceRef().getQueryAsForm();
                String appId = queryForm.getFirst("id").getValue();
                try {
                    TurtleManagers.appManager.uninstallApp(appId);
                    ret.success = true;
                } catch (WebAppException e) {
                    e.printStackTrace();
                }
            } else if ("/debug/shutdown".equals(requestPath)) {
                ret.success = true;
                ret.content = "not implemented";
            } else if ("/debug/api".equals(requestPath)) {
//                Form queryForm = request.getResourceRef().getQueryAsForm();
//                String api = queryForm.getFirst("api").getValue();
//                String accessToken = Configurations.getAccessToken();
            } else if ("/debug/alarm".equals(requestPath)) {
                final MediaPlayer mp = MediaPlayer.create(RestletWebService.this, R.raw.findme);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.release();
                    }
                });
            } else if ("/debug/native_apps".equals(requestPath)) {
                List<NativeApp> apps = new ArrayList<NativeApp>();
                List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(0);
                for (PackageInfo p : packageInfos) {
                    apps.add(new NativeApp(p.packageName, p.versionCode));
                }
                ret.success = true;
                ret.content = new Gson().toJson(apps);
            }
            response.setEntity(new StringRepresentation(new Gson().toJson(ret)));
        }

        public String getRunningApps() {
            return new Gson().toJson(TurtleManagers.appManager.getAllApps());
        }

        public String getAllUserData() {
            return new Gson().toJson(TurtleManagers.userDataManager.getAll(""));
        }

        public String getAccessToken() {
            return Configurations.getAccessToken();
        }

        public WebApp installApp(URL url) {
            WebApp ret = null;
            File tmpFile = null;
            try {
                tmpFile = File.createTempFile("turtl_debug_", ".tmp");
                FileUtils.copyURLToFile(url, tmpFile);
                ret = TurtleManagers.appManager.installApp(tmpFile);
            } catch (Exception e) {
                e.printStackTrace();
                if (tmpFile != null) {
                    FileUtils.deleteQuietly(tmpFile);
                }
            }
            return ret;
        }

        public class DebugInfo {
            boolean success;
            String content;
        }
    }

    public class ReaderRestlet extends Restlet {
        @Override
        public void handle(Request request, Response response) {
            Form queryForm = request.getResourceRef().getQueryAsForm();
            String type = queryForm.getFirst("type").getValue();
            String url = queryForm.getFirst("url").getValue();
            Logger.i("reader request," + type + "," + url);
            if (TextUtils.isEmpty(url)) {
                return;
            }
            if ("pdf".equals(type)) {
                if (!url.startsWith("http://")) {
                    url = Configurations.localHost + url;
                }
                url = Uri.encode(url, ",/?:@&=+$#");
                Logger.i("open pdf:" + url);
                try {
                    Uri uri = Uri.parse(url);
                    Intent readerIntent = new Intent(Intent.ACTION_VIEW, uri);
                    readerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    readerIntent.setPackage("com.adobe.reader");
                    startActivity(readerIntent);
                } catch (Exception e) {
                    Logger.e("start reader failed," + url);
                }
            }
        }
    }

}
