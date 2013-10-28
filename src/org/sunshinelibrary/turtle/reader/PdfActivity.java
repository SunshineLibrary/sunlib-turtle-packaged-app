package org.sunshinelibrary.turtle.reader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import org.sunshinelibrary.turtle.utils.Configurations;
import org.sunshinelibrary.turtle.utils.Logger;

/**
 * User: fxp
 * Date: 10/12/13
 * Time: 4:35 PM
 */
public class PdfActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("reader request,pdf");
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        String url = intent.getData().getQueryParameter("url");
        if (!TextUtils.isEmpty(url)) {
            if (!url.startsWith("http://")) {
                url = Configurations.localHost + url;
            }
            Logger.i("open pdf:" + url);
            try {
                Uri uri = Uri.parse(url);
                Intent readerIntent = new Intent(Intent.ACTION_VIEW, uri);
                readerIntent.setPackage("com.adobe.reader");
                startActivity(readerIntent);
            } catch (Exception e) {
                Logger.e("start reader failed," + url);
            }
        }
        finish();
    }
}
