package com.example.honey_create_cloud_pad.webclient;

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
        webSettings.setTextZoom(100);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowFileAccess(true); //设置可以访问文件

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");

    }


}
