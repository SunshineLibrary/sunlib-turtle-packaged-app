package org.sunshinelibrary.turtle.webservice;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.Predicate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.restlet.*;
import org.restlet.data.Form;
import org.restlet.data.Method;
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
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.appmanager.WebAppException;
import org.sunshinelibrary.turtle.models.NativeApp;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;

import java.io.File;
import java.io.IOException;
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
        // start the server
        // bind "/" to app with id==0
        // Setup a new instance

        // start server
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, Configurations.localPort);
        component.getClients().add(Protocol.FILE);
        component.getClients().add(Protocol.HTTP);

        final Router router = new Router();
        router.setDefaultMatchingMode(Template.MODE_STARTS_WITH);
        router.setRoutingMode(Router.MODE_FIRST_MATCH);

        // create API for user_data and apps
        router.attach("/exercise/v1/user_data/{path}", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                String requestPath = request.getResourceRef().getPath();
                String ret = "{}";
                String content = null;

                if (Method.GET.equals(request.getMethod())) {
                    Logger.i("GET user data," + requestPath);
                    ret = TurtleManagers.userDataManager.getData(requestPath);
                } else if (Method.POST.equals(request.getMethod())) {
                    Logger.i("POST user data," + requestPath);
                    try {
                        content = request.getEntity().getText();
                        TurtleManagers.userDataManager.sendData(requestPath, content);
                        ret = content;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.e("get body from request failed," + requestPath);
                    }
                }

                response.setEntity(new StringRepresentation(ret));
            }
        });


        router.attach("/reader", new ReaderRestlet());

        router.attach("/debug", new DebuggerRestlet());

        router.attach("/apps", new

                Restlet() {
                    @Override
                    public void handle(Request request, Response response) {
//                        TurtleManagers.appManager.refresh();
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

        ////////////////////////////////////////

        router.attach("/tracks", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                final String TAG = "tracks";
                String ret = null;
                if (Method.POST.equals(request.getMethod())) {
                    try {
                        Representation representation = request.getEntity();
                        JsonRepresentation jsonRepresentation = new JsonRepresentation(representation);
                        JSONObject jObject = jsonRepresentation.getJsonObject();
                        Log.i(TAG,"type:"+jsonRepresentation.toString()+"====>"+jObject.toString());

                        //TODO: add task to post data to t.sunshine & fix the issue that will cause post several times.
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG,"get body from request failed");
                    }
                }else{
                    ret = "api use error";
                    Log.e(TAG,"error use tracks api, should be http post");
                }
                response.setEntity(new StringRepresentation(ret));
            }
        });
        /////////////////////////////////////////

        router.attachDefault(new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                String requestPath = request.getResourceRef().getPath();
                if ("/".equals(requestPath)) {
                    response.redirectSeeOther("/app/0/index.html");
                    return;
                }
//                response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
//                response.setEntity(new StringRepresentation("<a href='/'>刷新</a>"));
                try {
                    response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                    response.setEntity(new InputRepresentation(getAssets().open("404.html")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Application application = new SimpleApplication(router);
        // serve all app folder
        component.getDefaultHost().attach("/app/", new FileApplication("file://" + Configurations.getAppBase()));
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
            return new Gson().toJson(TurtleManagers.userDataManager.getAll());
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
