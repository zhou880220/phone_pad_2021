package com.example.honey_create_cloud.webclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.example.honey_create_cloud.Constant;

public class MWebViewClient extends WebViewClient {
    private WebView webView;
    private Context context;
    private View web_error;
    private OnCityChangeListener onCityChangeListener;//定义对象

    public void setOnCityClickListener(OnCityChangeListener listener) {
        this.onCityChangeListener = listener;
    }

    public MWebViewClient(WebView webView) {
        this.webView = webView;
    }

    public MWebViewClient(WebView webView, Context context, View web_error) {
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
        if (!webView.getSettings().getLoadsImagesAutomatically()) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        if (onCityChangeListener != null) {
            onCityChangeListener.onCityClick(view.getUrl());
        }
        Log.i("url---", view.getUrl());
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return;
        }
        ChangErrorView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (request.isForMainFrame()) {
            ChangErrorView();
        }
    }

    private void ChangErrorView() {
        webView.setVisibility(View.GONE);
        web_error.setVisibility(View.VISIBLE);
    }

    public interface OnCityChangeListener {
        void onCityClick(String name);
    }
}
