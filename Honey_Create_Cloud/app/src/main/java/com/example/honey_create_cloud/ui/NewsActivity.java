package com.example.honey_create_cloud.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.util.ScreenAdapterUtil;
import com.example.honey_create_cloud.util.ShareSDK_Web;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MyWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsActivity extends AppCompatActivity {
    @InjectView(R.id.NewWebProgressbar)
    ProgressBar mNewWebProgressbar;
    @InjectView(R.id.new_Web_1)
    BridgeWebView mNewWeb;
    @InjectView(R.id.web_error)
    View mWebError;
    @InjectView(R.id.glide_gif)
    View mLoadingPage;
    private MWebChromeClient mWebChromeClient;

    private String TAG = "NewsActivity";

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
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            setAndroidNativeLightStatusBar(NewsActivity.this, true);//黑色字体
        }
        setContentView(R.layout.activity_news);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        webView(url);
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

        /**
         * 传递用户登录信息
         */
        mNewWeb.registerHandler("getUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                String userInfo = sb.getString("userInfo", "");
                if (!userInfo.isEmpty()) {
                    function.onCallBack(userInfo);
                } else {
                    Toast.makeText(NewsActivity.this, "获取用户数据异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初始页加载
     */
    private void mLodingTime() {
        ImageView imageView = findViewById(R.id.image_view);
        int res= R.drawable.loding_gif;
        Glide.with(this).
                load(res).placeholder(res).
                error(res).
                diskCacheStrategy(DiskCacheStrategy.NONE).
                into(imageView);
    }

    /**
     * JS交互
     */
    class MyJavaScriptInterface implements View.OnClickListener {
        private Context context;
        private ShareSDK_Web shareSDK_web;
        private PopupWindow popupWindow;

        public MyJavaScriptInterface(Context context) {
            this.context = context;
        }


        //关闭页面
        @JavascriptInterface
        public void backNewParams(String flag) {
            if (!flag.isEmpty()) {
                finish();
            } else {

            }
        }

        @JavascriptInterface
        public void goLogin(){
            SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
            SharedPreferences.Editor edit1 = sp1.edit();
            edit1.putString("apply_url", Constant.login_url);
            edit1.commit();
            finish();
        }

        //分享功能
        @JavascriptInterface
        public void shareSDKData(String shareData) {
            //集成分享类
//            shareSDK_web = new ShareSDK_Web(NewsActivity.this, shareData);
            View centerView = LayoutInflater.from(NewsActivity.this).inflate(R.layout.popupwindow, null);
            popupWindow = new PopupWindow(centerView, ViewGroup.LayoutParams.MATCH_PARENT,
                    400);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(centerView, Gravity.BOTTOM, 0, 0);

            View mCopyUrl = centerView.findViewById(R.id.copyurl);
            View mQrcode = centerView.findViewById(R.id.Qrcode);
            View mWeChat = centerView.findViewById(R.id.wechat);
            View mWeChatMoments = centerView.findViewById(R.id.wechatmoments);
            View mQq = centerView.findViewById(R.id.qq);
            TextView mDismiss = centerView.findViewById(R.id.popup_dismiss);

            mCopyUrl.setOnClickListener(this);
            mQrcode.setOnClickListener(this);
            mWeChat.setOnClickListener(this);
            mWeChatMoments.setOnClickListener(this);
            mQq.setOnClickListener(this);
            mDismiss.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.copyurl:
//                    shareSDK_web.CopyUrl();
                    popupWindow.dismiss();
                    break;
                case R.id.Qrcode:
//                    shareSDK_web.QRcode();
                    popupWindow.dismiss();
                    break;
                case R.id.wechat:
                    shareSDK_web.WechatshowShare();
                    popupWindow.dismiss();
                    break;
                case R.id.wechatmoments:
//                    shareSDK_web.WechatMomentsshowShare();
                    popupWindow.dismiss();
                    break;
                case R.id.qq:
//                    shareSDK_web.QQshowShare();
                    popupWindow.dismiss();
                    break;
                case R.id.popup_dismiss:
                    popupWindow.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * webview监听
     *
     * @param ead_web
     */
    private void wvClientSetting(BridgeWebView ead_web) {
        ead_web.setWebViewClient(new MyWebViewClient(ead_web));
        mWebChromeClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError,mLoadingPage);
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
