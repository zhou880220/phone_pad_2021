package com.example.honey_create_cloud.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.util.ScreenAdapterUtil;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsActivity extends AppCompatActivity {
    @InjectView(R.id.newwebprogressbar)
    ProgressBar mNewwebprogressbar;
    @InjectView(R.id.new_Web_1)
    WebView mNewWeb;
    @InjectView(R.id.web_error)
    View mWebError;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        boolean rects = ScreenAdapterUtil.hasNotchInScreen(this);
        if (rects == true) {
            //有刘海屏
            setAndroidNativeLightStatusBar(NewsActivity.this, false);//白色字体
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            getWindow().setAttributes(lp);
        } else if (rects == false) {
            //无刘海屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setAndroidNativeLightStatusBar(NewsActivity.this, true);//黑色字体
        }
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
    @SuppressLint("JavascriptInterface")
    private void webView(String url) {
        if (Build.VERSION.SDK_INT >= 19) {
            mNewWeb.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mNewWeb.getSettings().setLoadsImagesAutomatically(false);
        }
        WebSettings webSettings = mNewWeb.getSettings();
        if (webSettings != null) {
            WebViewSetting.initweb(webSettings);
        }
        mNewWeb.loadUrl(url);
        //js交互接口定义
        mNewWeb.addJavascriptInterface(new MyJavaScriptInterface(getApplicationContext()), "ApplyFunc");
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


    class MyJavaScriptInterface {
        private Context context;

        public MyJavaScriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void backNewParams(String flag) {
            if (!flag.isEmpty()) {
                finish();
            }
        }
    }

    /**
     * webview监听
     *
     * @param ead_web
     */
    private void wvClientSetting(WebView ead_web) {
        MWebViewClient mWebViewClient = new MWebViewClient(ead_web, this, mWebError);
        ead_web.setWebViewClient(mWebViewClient);
        MWebChromeClient mWebChromeClient = new MWebChromeClient(this, mNewwebprogressbar, mWebError);
        ead_web.setWebChromeClient(mWebChromeClient);
    }

    /**
     * 修改顶部状态栏字体颜色
     *
     * @param activity
     * @param dark
     */
    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}
