package com.example.honey_create_cloud_pad.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.honey_create_cloud_pad.R;
import com.example.honey_create_cloud_pad.webclient.MWebChromeClient;
import com.example.honey_create_cloud_pad.webclient.MWebViewClient;
import com.example.honey_create_cloud_pad.webclient.WebViewSetting;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsActivity extends AppCompatActivity {

    @InjectView(R.id.newwebprogressbar)
    ProgressBar mNewwebprogressbar;
    @InjectView(R.id.new_Web)
    WebView mNewWeb;
    @InjectView(R.id.web_error)
    View mWebError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_news);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        webView(url);
    }

    /**
     * webview初始化
     *
     * @param url
     */
    private void webView(String url) {
        if (Build.VERSION.SDK_INT >= 19) {
            mNewWeb.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mNewWeb.getSettings().setLoadsImagesAutomatically(false);
        }
        WebSettings webSettings = mNewWeb.getSettings();
        WebViewSetting.initweb(webSettings);
        mNewWeb.loadUrl(url);
        mNewWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mNewWeb != null && mNewWeb.canGoBack()) {
                        mNewWeb.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
        wvClientSetting(mNewWeb);
    }

    /**
     * webview监听
     *
     * @param ead_web
     */
    private void wvClientSetting(WebView ead_web) {
        MWebViewClient mWebViewClient = new MWebViewClient(ead_web, this, mWebError);
        ead_web.setWebViewClient(mWebViewClient);
        MWebChromeClient mWebChromeClient = new MWebChromeClient(this, mNewwebprogressbar,mWebError);
        ead_web.setWebChromeClient(mWebChromeClient);
    }
}
