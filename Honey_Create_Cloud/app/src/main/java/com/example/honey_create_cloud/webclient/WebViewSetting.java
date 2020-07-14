package com.example.honey_create_cloud.webclient;

import android.webkit.WebSettings;

public class WebViewSetting {
    private WebSettings webSettings;

    public WebViewSetting(WebSettings webSettings) {
        this.webSettings = webSettings;
    }

    public static void initweb(WebSettings webSettings) {
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowFileAccess(true); //设置可以访问文件

        //LOAD_DEFAULT：默认设置，当有缓存而且没有过期使用缓存，否则使用网络数据。
        //LOAD_CACHE_ELSE_NETWORK：只要有缓存就使用缓存，即使已经过期，否则使用网络数据。
        //LOAD_NO_CACHE：不适用缓存，只加载网络数据。
        //LOAD_CACHE_ONLY：不使用网络，只使用缓存数据。
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");
    }
}
