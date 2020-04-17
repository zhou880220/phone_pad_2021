package com.example.honey_create_cloud.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.PayBean;
import com.example.honey_create_cloud.util.ScreenAdapterUtil;
import com.example.honey_create_cloud.util.SystemUtil;
import com.example.honey_create_cloud.view.AnimationView;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IntentOpenActivity extends AppCompatActivity {
    @InjectView(R.id.newwebprogressbar)
    ProgressBar mNewwebprogressbar;
    @InjectView(R.id.intent_open_pay_web)
    BridgeWebView mIntentOpenPayWeb;
    @InjectView(R.id.web_error)
    View mWebError;
    @InjectView(R.id.loading_page)
    View mLoadingPage;

    private String type = "1";
    private String successPay = "支付成功";
    private String errorPay = "支付失败";
    private String purchaseOfEntry;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        boolean rects = ScreenAdapterUtil.hasNotchInScreen(this);
        if (rects == true) {
            //有刘海屏
            setAndroidNativeLightStatusBar(IntentOpenActivity.this, false);//白色字体
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            getWindow().setAttributes(lp);
        } else if (rects == false) {
            //无刘海屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setAndroidNativeLightStatusBar(IntentOpenActivity.this, true);//黑色字体
        }
        setContentView(R.layout.activity_intent_open);
        ButterKnife.inject(this);
        webView(Constant.payUrl);
        Intent intent = getIntent();
        purchaseOfEntry = intent.getStringExtra("PurchaseOfEntry");
        mLodingTime();

        //有方法名的都需要注册Handler后使用  获取版本号
        mIntentOpenPayWeb.registerHandler("getSystemVersion", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                function.onCallBack(SystemUtil.getSystemVersion());
            }
        });

//        //支付（支付宝/微信）
//        mIntentOpenPayWeb.registerHandler("openPay", new BridgeHandler() {
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                Toast.makeText(IntentOpenActivity.this, data, Toast.LENGTH_SHORT).show();
//            }
//        });

        //传递用户信息
        mIntentOpenPayWeb.registerHandler("getItemData", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                function.onCallBack(purchaseOfEntry);
            }
        });
    }


    class MJavaScriptInterface {
        private Context context;

        public MJavaScriptInterface(Context context) {
            this.context = context;
        }

        //联系客服
        @JavascriptInterface
        public void OpenPayIntent(String intentOpenPay) {
            Toast.makeText(context, intentOpenPay, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + intentOpenPay));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        //打开通知
        @JavascriptInterface
        public void openNotification() {
//            gotoSet();
        }

        //支付（支付宝/微信）
        @JavascriptInterface
        public void openPay(String data) {
            if (!data.isEmpty()) {
                Gson gson = new Gson();
                final PayBean payBean = gson.fromJson(data, PayBean.class);
                if (payBean.getType() == 1) {
                    mIntentOpenPayWeb.post(new Runnable() {
                        @SuppressLint("NewApi")
                        @Override
                        public void run() {
                            mIntentOpenPayWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + type + "\")", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    Log.e("www", "---");
                                }
                            });
                        }
                    });
                }
            }
        }

        @JavascriptInterface
        public void closePay(){
            // 广播通知
            Intent intent = new Intent();
            intent.setAction("action.refreshPay");
            sendBroadcast(intent);
            finish();
        }
    }

    private void webView(String url) {
        if (Build.VERSION.SDK_INT >= 19) {
            mIntentOpenPayWeb.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mIntentOpenPayWeb.getSettings().setLoadsImagesAutomatically(false);
        }
        WebSettings webSettings = mIntentOpenPayWeb.getSettings();
        if (webSettings != null) {
            WebViewSetting.initweb(webSettings);
        }
        mIntentOpenPayWeb.loadUrl(url);
        //js交互接口定义
        mIntentOpenPayWeb.addJavascriptInterface(new MJavaScriptInterface(getApplicationContext()), "ApplyFunc");
        mIntentOpenPayWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mIntentOpenPayWeb != null && mIntentOpenPayWeb.canGoBack()) {
                        mIntentOpenPayWeb.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
        wvClientSetting(mIntentOpenPayWeb);

    }

    private void wvClientSetting(WebView mIntentOpenPay) {
//        MWebViewClient mWebViewClient = new MWebViewClient(mIntentOpenPay, this, mWebError);
//        mIntentOpenPay.setWebViewClient(mWebViewClient);
        MWebChromeClient mWebChromeClient = new MWebChromeClient(this, mNewwebprogressbar, mWebError);
        mIntentOpenPay.setWebChromeClient(mWebChromeClient);
    }

    /**
     * 初始页加载
     */
    private void mLodingTime() {
        final AnimationView hideAnimation = new AnimationView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideAnimation.getHideAnimation(mLoadingPage, 500);
                mLoadingPage.setVisibility(View.GONE);
            }
        }, 3000);
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

    @Override
    protected void onDestroy() {
        if (mIntentOpenPayWeb != null) {
            mIntentOpenPayWeb.loadUrl(null);
            mIntentOpenPayWeb.clearHistory();
            ((ViewGroup) mIntentOpenPayWeb.getParent()).removeView(mIntentOpenPayWeb);
            mIntentOpenPayWeb.destroy();
            mIntentOpenPayWeb = null;
        }
        super.onDestroy();
    }
}
