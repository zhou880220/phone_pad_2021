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
    private String TAG = "TAG";
    private View mLoadingPage;



    private OnCityChangeListener onCityChangeListener;//定义对象

    public void setOnCityClickListener(OnCityChangeListener listener) {
        this.onCityChangeListener = listener;
    }

    public MyWebViewClient(BridgeWebView webView,View mLoadingPage) {
        super(webView);
        this.mLoadingPage = mLoadingPage;
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
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mLoadingPage.setVisibility(View.GONE);
    }


    @Override
    public void onLoadResource(WebView view, String url) {
        if (onCityChangeListener != null) {
            onCityChangeListener.onCityClick(view.getUrl());
        }
        super.onLoadResource(view, url);
    }

    public interface OnCityChangeListener {
        void onCityClick(String name);
    }
}