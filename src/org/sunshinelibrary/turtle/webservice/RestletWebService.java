package org.sunshinelibrary.turtle.webservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.android.internal.util.Predicate;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.restlet.*;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.sunshinelibrary.turtle.TurtleManagers;
import org.sunshinelibrary.turtle.models.WebApp;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;

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
        component.getServers().add(Protocol.HTTP, 8182);
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
        Logger.i("RestletWebService onStartCommand," + intent.getAction());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class DebuggerRestlet extends Restlet {

        @Override
        public void handle(Request request, Response response) {
            String requestPath = request.getResourceRef().getPath();
            String ret = "no such debug handler";
            if ("/debug/running_apps".equals(requestPath)) {
                ret = getRunningApps();
            } else if ("/debug/userdata".equals(requestPath)) {
                ret = getAllUserData();
            }
            response.setEntity(new StringRepresentation(ret));
        }

        public String getRunningApps() {
            return new Gson().toJson(TurtleManagers.appManager.getAllApps());
        }

        public String getAllUserData() {
            return new Gson().toJson(TurtleManagers.userDataManager.getAll());
        }
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

}
