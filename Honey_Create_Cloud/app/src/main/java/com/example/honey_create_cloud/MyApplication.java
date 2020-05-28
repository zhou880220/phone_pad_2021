package com.example.honey_create_cloud;

import android.app.Application;
import android.webkit.WebView;

/**
 * Created by wangpan on 2020/5/27
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WebView webView = new WebView(getApplicationContext());
        webView.loadUrl(Constant.text_url);
    }
}
