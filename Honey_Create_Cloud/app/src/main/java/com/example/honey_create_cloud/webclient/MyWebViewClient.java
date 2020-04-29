package com.example.honey_create_cloud.webclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;

/**
 * Created by wangpan on 2020/4/29
 */
public class MyWebViewClient extends BridgeWebViewClient {
    private BridgeWebView webView;
    private Context context;
    private View web_error;
    private MWebViewClient.OnCityChangeListener onCityChangeListener;//定义对象

    public void setOnCityClickListener(MWebViewClient.OnCityChangeListener listener) {
        this.onCityChangeListener = listener;
    }

    public MyWebViewClient(BridgeWebView webView, Context context, View web_error) {
        super(webView);
        this.webView = webView;
        this.context = context;
        this.web_error = web_error;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url == null) return false;
        if (url.startsWith("http:") || url.startsWith("https:")) {
            view.loadUrl(url);
            return false;
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (Exception e) {
                // ToastUtils.showShort("暂无应用打开此链接");
            }
        }
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

    }

    @Override
    public void onPageFinished(WebView view, String url) {

        super.onPageFinished(view, url);
    }
}
