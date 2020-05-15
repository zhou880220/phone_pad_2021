package com.example.honey_create_cloud.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alipay.sdk.app.PayTask;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.AppOrderInfo;
import com.example.honey_create_cloud.bean.PayBean;
import com.example.honey_create_cloud.bean.PayType;
import com.example.honey_create_cloud.bean.WxPayBean;
import com.example.honey_create_cloud.util.PayResult;
import com.example.honey_create_cloud.util.ScreenAdapterUtil;
import com.example.honey_create_cloud.view.AnimationView;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MyWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class IntentOpenActivity extends AppCompatActivity {
    @InjectView(R.id.NewWebProgressbar)
    ProgressBar mNewWebProgressbar;
    @InjectView(R.id.intent_open_pay_web)
    BridgeWebView mIntentOpenPayWeb;
    @InjectView(R.id.web_error)
    View mWebError;
    @InjectView(R.id.loading_page)
    View mLoadingPage;

    private int paySuccess = 1;
    private int payError = 2;
    private String purchaseOfEntry;
    private String appId;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private String TAG = "TAG";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        mIntentOpenPayWeb.post(new Runnable() {
                            @SuppressLint("NewApi")
                            @Override
                            public void run() {
                                mIntentOpenPayWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + "1" + "\")", new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        Log.e("wangpan", "---");
                                    }
                                });
                            }
                        });
                        Toast.makeText(IntentOpenActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Log.e("wangpan", payResult + "");
                    } else if (TextUtils.equals(resultStatus, "4000")) {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        //此功能用于在用户支付后回调通知页面支付成功/失败
                        mIntentOpenPayWeb.post(new Runnable() {
                            @SuppressLint("NewApi")
                            @Override
                            public void run() {
                                mIntentOpenPayWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + "2" + "\")", new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        Log.e("wangpan", "---");
                                    }
                                });
                            }
                        });
                        Toast.makeText(IntentOpenActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        Log.e("wangpan", payResult + "");
                    } else if (TextUtils.equals(resultStatus, "8000")) {
                        Toast.makeText(IntentOpenActivity.this, "正在处理中...", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        Toast.makeText(IntentOpenActivity.this, "支付未完成,用户取消", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "5000")) {
                        Toast.makeText(IntentOpenActivity.this, "重复请求", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(IntentOpenActivity.this, "支付异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
//                case SDK_AUTH_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
//                    String resultStatus = authResult.getResultStatus();
//
//                    // 判断resultStatus 为“9000”且result_code
//                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
//                        // 传入，则支付账户为该授权账户
//                        showAlert(PayDemoActivity.this, getString(R.string.auth_success) + authResult);
//                    } else {
//                        // 其他状态值则为授权失败
//                        showAlert(PayDemoActivity.this, getString(R.string.auth_failed) + authResult);
//                    }
//                    break;
//                }
                default:
                    break;
            }
        }
    };
    private String token;


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
        EventBus.getDefault().register(this);
        webView(Constant.test_shoppingCart);
        Intent intent = getIntent();
        purchaseOfEntry = intent.getStringExtra("PurchaseOfEntry");
        appId = intent.getStringExtra("appId");
        token = intent.getStringExtra("token");
        Log.e("wangpan", "onCreate: " + token);
        mLodingTime();


    }

    @SuppressLint("NewApi")
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
        wvClientSetting(mIntentOpenPayWeb);


        /**
         * 传递用户订单信息
         */
        mIntentOpenPayWeb.registerHandler("getItemData", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                function.onCallBack(purchaseOfEntry);
            }
        });
        /**
         * 传递用户登录信息
         */
        mIntentOpenPayWeb.registerHandler("getUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                String userInfo = sb.getString("userInfo", "");
                if (!userInfo.isEmpty()) {
                    function.onCallBack(userInfo);
                } else {
                    Toast.makeText(IntentOpenActivity.this, "获取用户数据异常", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mIntentOpenPayWeb.registerHandler("getAppId", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (!appId.isEmpty()) {
                    function.onCallBack(appId);
                }
            }
        });

    }

    class MJavaScriptInterface {
        private Context context;

        public MJavaScriptInterface(Context context) {
            this.context = context;
        }

        //支付（支付宝/微信）
        @JavascriptInterface
        public void openPay(String data) {
            if (!data.isEmpty()) {
                Gson gson = new Gson();
                PayBean payBean = gson.fromJson(data, PayBean.class);
                Log.e(TAG, "openPay: " + data);
                if (payBean.getPayType().equals("alipay")) { // 支付宝支付
                    alipaytypeOkhttp(payBean);
                } else if (payBean.getPayType().equals("weixin")) { //微信支付
                    wxpaytypeOkhttp(payBean);
                }
            }
        }

        //联系客服  打开通讯录
        @JavascriptInterface
        public void OpenPayIntent(String intentOpenPay) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + intentOpenPay));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        /**
         * 支付成功/失败  用户点击返回按钮关闭页面前通知第三方 刷新数据
         */
        @JavascriptInterface
        public void closePay() {
            // 广播通知
            Intent intent = new Intent();
            intent.setAction("action.refreshPay");
            sendBroadcast(intent);
            finish();
        }

        /**
         * 返回关闭支付页面
         */
        @JavascriptInterface
        public void goThirdApply() {
            finish();
        }
    }

    private void alipaytypeOkhttp(final PayBean payBean) {
        String formBody = "{" +
                "userId:'" + payBean.getUserId() + '\'' +
                ", outTradeNo:'" + payBean.getOutTradeNo() + '\'' +
                ", PayType:'" + payBean.getPayType() + '\'' +
                '}';
        MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, formBody);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.payType)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e(TAG, "onResponse: " + string);
                Gson gson = new Gson();
                PayType payType = gson.fromJson(string, PayType.class);
                if (payType.getCode() == 200) {
                    alipayOkhttp(payBean);
                }
            }
        });

    }

    private void wxpaytypeOkhttp(final PayBean payBean) {
        String formBody = "{" +
                "userId:'" + payBean.getUserId() + '\'' +
                ", outTradeNo:'" + payBean.getOutTradeNo() + '\'' +
                ", PayType:'" + payBean.getPayType() + '\'' +
                '}';
        MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, formBody);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.payType)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e(TAG, "onResponse: " + string);
                Gson gson = new Gson();
                PayType payType = gson.fromJson(string, PayType.class);
                if (payType.getCode() == 200) {
                    wxPayOkhttp(payBean);
                }
            }
        });
    }

    private void alipayOkhttp(PayBean payBean) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(Constant.appOrderInfo + payBean.getOutTradeNo())
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    String string = response.body().string();
                    Log.e(TAG, "onResponse: " + string);
                    Gson gson = new Gson();
                    AppOrderInfo appOrderInfo = gson.fromJson(string, AppOrderInfo.class);
                    //orderInfo为通过接口获取的订单信息中的url
                    final String orderInfo = appOrderInfo.getData();
                    final Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(IntentOpenActivity.this);
                            Map<String, String> result = alipay.payV2(orderInfo, true);

                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else {

                }
            }
        });
    }

    private void wxPayOkhttp(PayBean payBean) {
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        // 将该app注册到微信
        msgApi.registerApp(Constant.APP_ID);

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(Constant.wxPay_appOrderInfo + payBean.getOutTradeNo())
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    String string = response.body().string();
                    Gson gson = new Gson();
                    WxPayBean wxPayBean = gson.fromJson(string, WxPayBean.class);
                    PayReq request = new PayReq();
                    request.appId = wxPayBean.getData().getAppid();
                    request.partnerId = wxPayBean.getData().getPartnerid();
                    request.prepayId = wxPayBean.getData().getPrepayid();
                    request.packageValue = wxPayBean.getData().getWxPackage();
                    request.nonceStr = wxPayBean.getData().getNoncestr();
                    request.timeStamp = wxPayBean.getData().getTimestamp();
                    request.sign = wxPayBean.getData().getSign();
                    msgApi.sendReq(request);
                    Log.e(TAG, "onResponse: " + string);
                } else {

                }
            }
        });
    }


    /**
     * 初始化
     *
     * @param mIntentOpenPay
     */
    private void wvClientSetting(BridgeWebView mIntentOpenPay) {
        mIntentOpenPay.setWebViewClient(new MyWebViewClient(mIntentOpenPay));
        MWebChromeClient mWebChromeClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError);
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

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onMessageEvent(String event) {
        if (event.equals("支付成功")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            mIntentOpenPayWeb.post(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    mIntentOpenPayWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + "1" + "\")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.e("wangpan", "---");
                        }
                    });
                }
            });
        } else if (event.equals("支付失败")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            mIntentOpenPayWeb.post(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    mIntentOpenPayWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + "2" + "\")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.e("wangpan", "---");
                        }
                    });
                }
            });
        }
        Toast.makeText(this, event, Toast.LENGTH_SHORT).show();
    }

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                exitTime = System.currentTimeMillis();
                new AlertDialog.Builder(this)
                        .setTitle(R.string.app_tip)
                        .setMessage(R.string.close_page)
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 清除页面数据
     */
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
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
