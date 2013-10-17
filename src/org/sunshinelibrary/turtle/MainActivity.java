package org.sunshinelibrary.turtle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.context = this;

        findViewById(R.id.button).setOnClickListener(new AddClick());
        findViewById(R.id.button1).setOnClickListener(new QueryClick());
    }


    public class AddClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            WebApp app = new WebApp(context);
//            app.localFolder = "123";
//            app.id = "id123";
//            app.save();
//            onDao dao = new PersonDao(getContext());
        }
    }

    public class QueryClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            List<WebApp> apps = WebApp.getAll();
//            Log.i("WebAppManager",
//                    new Gson().toJson(WebApp.getById("id123"))
//                    new Gson().toJson(apps)
//            );

        }
    }

}
