package com.example.honey_create_cloud_pad.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.honey_create_cloud_pad.Constant;
import com.example.honey_create_cloud_pad.R;
import com.example.honey_create_cloud_pad.view.AnimationView;
import com.example.honey_create_cloud_pad.webclient.MWebChromeClient;
import com.example.honey_create_cloud_pad.webclient.MyWebViewClient;
import com.example.honey_create_cloud_pad.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity {

    @BindView(R.id.newwebprogressbar)
    ProgressBar mNewWebProgressbar;
    @BindView(R.id.new_Web_1)
    BridgeWebView mNewWeb;
    @BindView(R.id.web_error)
    View mWebError;
    @BindView(R.id.loading_page)
    View mLoadingPage;
    private MWebChromeClient mWebChromeClient;


    private String goBackUrl;
    private String TAG = "NewsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String from = intent.getStringExtra("from");
        webView("http://172.16.23.156/mobileInformation?id=253");
        mLodingTime();
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
        String userAgentString = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgentString + "; application-center");
        WebViewSetting.initweb(webSettings);
        mNewWeb.loadUrl(url);
        //js交互接口定义
        mNewWeb.addJavascriptInterface(new MyJavaScriptInterface(getApplicationContext()), "ApplyFunc");
        mNewWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mNewWeb != null && mNewWeb.canGoBack()) {
                        if (goBackUrl.contains("/mobileInformation")) { //咨询页面返回拦截
                            finish();
                        } else {
                            mNewWeb.goBack();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        wvClientSetting(mNewWeb);

        //传递用户登录信息
        mNewWeb.registerHandler("getUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                    String userInfo = sb.getString("userInfo", "");
                    if (!userInfo.isEmpty()) {
                        function.onCallBack(userInfo);
                    } else {
                        function.onCallBack("false");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //用户登录异常回调登录页
        mNewWeb.registerHandler("goLogin", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                    SharedPreferences.Editor edit1 = sp1.edit();
                    edit1.putString("apply_url", Constant.login_url);
                    edit1.commit();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //接口废弃
        mNewWeb.registerHandler("backNewParams", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                finish();
            }
        });

//        /**
//         * 分享更具传递的type类型进行分享的页面
//         */
//        mNewWeb.registerHandler("shareInterface", new BridgeHandler() {
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                try {
//                    if (!data.isEmpty()) {
//                        Log.e(TAG, "shareInterface: " + data);
//                        //微信初始化
//                        wxApi = WXAPIFactory.createWXAPI(NewsActivity.this, Constant.APP_ID);
//                        wxApi.registerApp(Constant.APP_ID);
//                        //QQ初始化
//                        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, NewsActivity.this);
//
//                        Map map = JSONObject.parseObject(data, Map.class);
//                        String num = (String) map.get("obj");
//                        Map mapType = JSONObject.parseObject(num, Map.class);
//                        int type = (int) mapType.get("type");
//                        String value = String.valueOf(mapType.get("data"));
//                        Gson gson = new Gson();
//                        ShareSdkBean shareSdkBean = gson.fromJson(value, ShareSdkBean.class);
//                        if (type == 1) {
//                            boolean wxAppInstalled = isWxAppInstalled(NewsActivity.this);
//                            if (wxAppInstalled == true) {
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        wechatShare(0, shareSdkBean); //好友
//                                    }
//                                }).start();
//                            } else {
//                                Toast.makeText(NewsActivity.this, "手机未安装微信", Toast.LENGTH_SHORT).show();
//                            }
//                        } else if (type == 2) {
//                            boolean wxAppInstalled1 = isWxAppInstalled(NewsActivity.this);
//                            if (wxAppInstalled1 == true) {
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        wechatShare(1, shareSdkBean); //朋友圈
//                                    }
//                                }).start();
//                            } else {
//                                Toast.makeText(NewsActivity.this, "手机未安装微信", Toast.LENGTH_SHORT).show();
//                            }
//                        } else if (type == 3) {
//                            boolean qqClientAvailable = isQQClientAvailable(NewsActivity.this);
//                            if (qqClientAvailable == true) {
//                                qqFriend(shareSdkBean);
//                            } else {
//                                Toast.makeText(NewsActivity.this, "手机未安装QQ", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                } catch (JsonSyntaxException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

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
     * JS交互
     */
    class MyJavaScriptInterface {
        private Context context;

        public MyJavaScriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void backNewParams(String flag) {
            Log.e(TAG, flag);
            if (!flag.isEmpty()) {
                finish();
            } else {

            }
        }
    }

    /**
     * webview监听
     *
     * @param ead_web
     */
    private void wvClientSetting(BridgeWebView ead_web) {
        MyWebViewClient myWebViewClient = new MyWebViewClient(ead_web, mWebError);
        ead_web.setWebViewClient(myWebViewClient);
        myWebViewClient.setOnCityClickListener(new MyWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {
                goBackUrl = name;
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                Log.e(TAG, "onCityClick: " + name);
            }
        });
        mWebChromeClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError, mLoadingPage);
        ead_web.setWebChromeClient(mWebChromeClient);
    }
}
