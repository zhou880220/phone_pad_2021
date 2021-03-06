package com.example.honey_create_cloud.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.StartPageActivity;
import com.example.honey_create_cloud.bean.ShareSdkBean;
import com.example.honey_create_cloud.bean.ShareSdkPackages;
import com.example.honey_create_cloud.util.BaseUtils;
import com.example.honey_create_cloud.util.QMUITouchableSpan;
import com.example.honey_create_cloud.util.SystemUtil;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MyWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.leolin.shortcutbadger.ShortcutBadger;

public class AppDetailActivity extends AppCompatActivity {

    private static final String TAG = "AppDetailActivity";
    /***********************view*******************************/
    @InjectView(R.id.NewWebProgressbar)
    ProgressBar mNewWebProgressbar;
    @InjectView(R.id.bwv_view)
    BridgeWebView mNewWeb;
    @InjectView(R.id.web_error)
    View mWebError;
    @InjectView(R.id.glide_gif)
    View mLoadingPage;
    @InjectView(R.id.closeLoginPage)
    ImageView mCloseLoginPage;
    @InjectView(R.id.text_policy_reminder)
    TextView mTextPolicyReminder;
    @InjectView(R.id.text_policy_reminder_back)
    RelativeLayout mTextPolicyReminderBack;
    /***********************prams*******************************/
    private String url;
    private String token;
    private String userid;
    private String appId;
    private String zxIdTouTiao;
    /***********************object*******************************/
    private Context mContext;
    //??????
    private IWXAPI wxApi;
    public static Tencent mTencent;
    private String PolicyAndReminder = "???????????????????????????????????????";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        ButterKnife.inject(this);
        mContext = this;
        intiData();
        initClick();
        mLodingTime();
        try {
            if (!url.isEmpty()) {
                webView(url);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTextPolicyReminder.setText(generateSp(PolicyAndReminder));
        mTextPolicyReminder.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void intiData() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        token = intent.getStringExtra("token");
        userid = intent.getStringExtra("userid");
        appId = intent.getStringExtra("appId");
        zxIdTouTiao = intent.getStringExtra("zxIdTouTiao");
    }

    /**
     * ???????????????
     */
    private void mLodingTime() {
        ImageView imageView = findViewById(R.id.image_view);
        int res = R.drawable.loding_gif;
        Glide.with(this).
                load(res).placeholder(res).
                error(res).
                diskCacheStrategy(DiskCacheStrategy.NONE).
                into(imageView);
    }


    /**
     * webview?????????
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
        String userAgentString = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgentString + "; application-center");
        if (webSettings != null) {
            WebViewSetting.initweb(webSettings);
        }
        mNewWeb.loadUrl(url);
        wvClientSetting(mNewWeb);

        /**
         * ???????????????
         */
        mNewWeb.registerHandler("closeDetail", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    finish();
                    function.onCallBack("success");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //???????????????????????????????????????
        mNewWeb.registerHandler("showApplyParams", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Log.e(TAG, "???????????????:1 " + data);
                        Map map = JSONObject.parseObject(data, Map.class);
                        String redirectUrl = (String) map.get("redirectUrl");
                        String currentUrl = mNewWeb.getUrl();
                        Log.e(TAG, "currentUrl: "+currentUrl );
                        int appLyId = (int) map.get("appId");
                        int toDetail = map.get("toDetail") ==null ? 0 :(int) map.get("toDetail");
                        String appId = String.valueOf(appLyId);
                        if (!redirectUrl.isEmpty()) {
                            Log.e(TAG, "???????????????:2 " + redirectUrl);
                            if (toDetail == 1) {//????????????????????????????????????
                                webView(redirectUrl);
                            }else {
                                if (zxIdTouTiao == null || zxIdTouTiao.isEmpty()) {
                                    Log.e(TAG, "???????????????:3 " + redirectUrl);
                                    Intent intent = new Intent(mContext, ApplyFirstActivity.class);
                                    intent.putExtra("url", redirectUrl);
                                    intent.putExtra("token", token);
                                    intent.putExtra("userid", userid);
                                    intent.putExtra("appId", appId);
                                    intent.putExtra("isFromHome", currentUrl.contains("apply") ? "0": "1");
                                    startActivity(intent);
                                } else {
                                    Log.e(TAG, "???????????????:4" + redirectUrl);
                                    Intent intent = new Intent(mContext, ApplyFirstActivity.class);
                                    intent.putExtra("url", redirectUrl);
                                    intent.putExtra("token", token);
                                    intent.putExtra("userid", userid);
                                    intent.putExtra("appId", appId);
                                    intent.putExtra("zxIdTouTiao", zxIdTouTiao);
                                    intent.putExtra("isFromHome", currentUrl.contains("apply") ? "0": "1");
                                    startActivity(intent);
                                    zxIdTouTiao = "";
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * ?????????????????????type???????????????????????????
         */
        mNewWeb.registerHandler("shareInterface", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                boolean isShareSuc = false;
                try {
                    Log.e(TAG, "shareInterface: " + data);
                    if (!data.isEmpty()) {
                        //???????????????
                        wxApi = WXAPIFactory.createWXAPI(mContext, Constant.APP_ID);
                        wxApi.registerApp(Constant.APP_ID);
                        //QQ?????????
                        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, mContext);
                        ShareSdkPackages shareSdkPackages = new Gson().fromJson(data, ShareSdkPackages.class);
                        int type = shareSdkPackages.getType();
                        Log.e(TAG, "type: " + type);
                        ShareSdkBean shareSdkBean = shareSdkPackages.getData();
                        Log.e(TAG, "url: " + shareSdkBean.getUrl());
                        if (type == 1) {
                            boolean wxAppInstalled = BaseUtils.isWxAppInstalled(mContext);
                            if (wxAppInstalled == true) {
                                isShareSuc = true;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        wechatShare(0, shareSdkBean); //??????
                                        BaseUtils.wechatShare(wxApi, 0, shareSdkBean);
                                    }
                                }).start();
                            } else {
                                Toast.makeText(mContext, "?????????????????????", Toast.LENGTH_SHORT).show();
                            }
                        } else if (type == 2) {
                            boolean wxAppInstalled1 = BaseUtils.isWxAppInstalled(mContext);
                            if (wxAppInstalled1 == true) {
                                isShareSuc = true;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        wechatShare(1, shareSdkBean); //?????????
                                        BaseUtils.wechatShare(wxApi, 1, shareSdkBean);
                                    }
                                }).start();
                            } else {
                                Toast.makeText(mContext, "?????????????????????", Toast.LENGTH_SHORT).show();
                            }
                        } else if (type == 3) {
                            boolean qqClientAvailable = BaseUtils.isQQClientAvailable(mContext);
                            if (qqClientAvailable == true) {
                                isShareSuc = true;
                                qqFriend(shareSdkBean);
                            } else {
                                Toast.makeText(mContext, "???????????????QQ", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                function.onCallBack(isShareSuc+"");
            }
        });
    }

    private void initClick() {
        //?????????????????????????????????????????? ????????????
        mCloseLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    finish();
//                    if (mNewWeb.canGoBack()) {
//                        webView(Constant.text_url);
//                        mCloseLoginPage.setVisibility(View.GONE);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
        String apply_url = sp1.getString("apply_url", "");//???????????????????????????????????????????????????
        Log.e(TAG, " onStart: "+ apply_url);
        if (!TextUtils.isEmpty(apply_url)) {
            webView(apply_url);
        }
        SharedPreferences.Editor edit = sp1.edit();
        edit.clear();
        edit.commit();
        super.onStart();
    }

    /**
     * webview??????
     *
     * @param ead_web
     */
    private void wvClientSetting(BridgeWebView ead_web) {
        MyWebViewClient myWebViewClient = new MyWebViewClient(ead_web, mWebError);
        ead_web.setWebViewClient(myWebViewClient);
        myWebViewClient.setOnCityClickListener(new MyWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {
//                goBackUrl = name;
                Log.e(TAG, "onCityClick: " + name);
                WebBackForwardList webBackForwardList = mNewWeb.copyBackForwardList();
                boolean b = webBackForwardList.getCurrentIndex() != webBackForwardList.getSize() - 1;
                if (name != null) {
                    if (name.equals(Constant.login_url)) {
                        mTextPolicyReminder.setVisibility(View.VISIBLE);
                        mCloseLoginPage.setVisibility(View.VISIBLE);
                        mTextPolicyReminderBack.setVisibility(View.VISIBLE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    }else {
                        mTextPolicyReminder.setVisibility(View.GONE);
                        mCloseLoginPage.setVisibility(View.GONE);
                        mTextPolicyReminderBack.setVisibility(View.GONE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    }
                }
            }
        });
        MWebChromeClient mWebChromeClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError, mLoadingPage);
//        ead_web.setWebChromeClient(mWebChromeClient);

        ead_web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //???????????????
                    if (mLoadingPage != null) {
                        mLoadingPage.setVisibility(View.GONE);
                        mNewWebProgressbar.setVisibility(View.GONE);
                    } else {
                        mNewWebProgressbar.setVisibility(View.GONE);
                    }
                } else {
                    //???????????????
                    mNewWebProgressbar.setVisibility(View.VISIBLE);
                    mNewWebProgressbar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
//                uploadMessage = valueCallback;
//                openFileChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
//                uploadMessage = valueCallback;
//                openFileChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
//                uploadMessage = valueCallback;
//                openFileChooserActivity();
            }

            // For Android >= 5.0 ??????????????????????????????
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                String[] acceptTypes = fileChooserParams.getAcceptTypes();
//                uploadMessageAboveL = filePathCallback;
                Log.e(TAG, "onShowFileChooser:?????????????????? " + acceptTypes[0]);
                if (acceptTypes[0].equals("*/*")) {
//                    openFileChooserActivity(); //??????????????????
                } else if (acceptTypes[0].equals("image/*")) {
//                    openImageChooserActivity();//?????????????????????????????????
                } else if (acceptTypes[0].equals("video/*")) {
//                    openVideoChooserActivity();//??????????????????/????????????
                }
                return true;
            }
        });
    }

    /**
     * @param text ??????????????????
     * @return
     */
    private SpannableString generateSp(String text) {
        //???????????????????????????
        String high_light_1 = "??????????????????";
        String high_light_2 = "??????????????????";

        SpannableString spannableString = new SpannableString(text);
        //????????????
        int start = 0;
        //????????????
        int end;
        int index;
        //indexOf(String str, int fromIndex): ????????? fromIndex ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? -1???
        //???????????????(index = text.indexOf(high_light_1, start)) > -1????????????????????????????????????????????????????????????high_light_1????????????????????????????????????????????????
        while ((index = text.indexOf(high_light_1, start)) > -1) {
            //???????????????
            end = index + high_light_1.length();
            spannableString.setSpan(new QMUITouchableSpan(this.getResources().getColor(R.color.blue_PolicyAndReminder), this.getResources().getColor(R.color.blue_PolicyAndReminder),
                    this.getResources().getColor(R.color.white_PolicyAndReminder), this.getResources().getColor(R.color.white_PolicyAndReminder)) {
                @Override
                public void onSpanClick(View widget) {
                    Intent intent = new Intent(mContext, ReminderActivity.class);
                    intent.putExtra("type", "1");
                    startActivity(intent);
                }
            }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end;
        }

        start = 0;
        while ((index = text.indexOf(high_light_2, start)) > -1) {
            end = index + high_light_2.length();
            spannableString.setSpan(new QMUITouchableSpan(this.getResources().getColor(R.color.blue_PolicyAndReminder), this.getResources().getColor(R.color.blue_PolicyAndReminder),
                    this.getResources().getColor(R.color.white_PolicyAndReminder), this.getResources().getColor(R.color.white_PolicyAndReminder)) {
                @Override
                public void onSpanClick(View widget) {
                    // ????????????????????????????????????????????????WebView?????????????????????
                    Intent intent = new Intent(mContext, ReminderActivity.class);
                    intent.putExtra("type", "2");
                    startActivity(intent);
                }
            }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end;
        }
        //????????????SpannableString
        return spannableString;
    }


    /**
     * ?????????QQ??????
     */
    int shareType = 1;
    //IMG
    public static String IMG = "";
    int mExtarFlag = 0x00;

    private void qqFriend(ShareSdkBean shareSdkBean) {
        final Bundle params = new Bundle();
        //
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareSdkBean.getTitle()); //???????????????
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareSdkBean.getUrl());//???????????????
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareSdkBean.getTxt());//???????????????

        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareSdkBean.getIcon());//???????????????
//        params.putString(shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL
//                : QQShare.SHARE_TO_QQ_IMAGE_URL, IMG);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getPackageName());
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

        doShareToQQ(params);
        return;
    }

    private void doShareToQQ(final Bundle params) {
        // QQ????????????????????????
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQQ(AppDetailActivity.this, params, qqShareListener);
                }
            }
        });
    }

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
            }
        }

        @Override
        public void onComplete(Object response) {
        }

        @Override
        public void onError(UiError e) {
        }
    };

}