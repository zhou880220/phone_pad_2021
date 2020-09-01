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

        webSettings.setSupportZoom(false);
        webSettings.setTextZoom(100);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowFileAccess(true); //设置可以访问文件

        //LOAD_DEFAULT：默认的缓存使用模式。在进行页面前进或后退的操作时，如果缓存可用并未过期就优先加载缓存，否则从网络上加载数据。这样可以减少页面的网络请求次数。
        //LOAD_CACHE_ELSE_NETWORK：只要缓存可用就加载缓存，哪怕它们已经过期失效。如果缓存不可用就从网络上加载数据。
        //LOAD_NO_CACHE：不加载缓存，只从网络加载数据。
        //LOAD_CACHE_ONLY：不从网络加载数据，只从缓存加载数据。
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");
    }
}
