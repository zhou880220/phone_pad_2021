package com.example.honey_create_cloud.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.AppOrderInfo;
import com.example.honey_create_cloud.bean.PayBean;
import com.example.honey_create_cloud.bean.PayType;
import com.example.honey_create_cloud.bean.WxPayBean;
import com.example.honey_create_cloud.util.MyDialog;
import com.example.honey_create_cloud.util.PayResult;
import com.example.honey_create_cloud.util.ScreenAdapterUtil;
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
import com.xj.library.utils.ToastUtils;

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
    @InjectView(R.id.glide_gif)
    View mLoadingPage;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                     */
                    String resultInfo = payResult.getResult();// ?????????????????????????????????
                    String resultStatus = payResult.getResultStatus();
                    // ??????resultStatus ???9000?????????????????????
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // ??????????????????????????????????????????????????????????????????????????????
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
                                mIntentOpenPayWeb.callHandler("paymentFeedback", "1", new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {

                                    }
                                });
                            }
                        });
                        Toast.makeText(IntentOpenActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        Log.e("wangpan", payResult + "");
                    } else if (TextUtils.equals(resultStatus, "4000")) {
                        // ???????????????????????????????????????????????????????????????????????????
                        //???????????????????????????????????????????????????????????????/??????
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
                                mIntentOpenPayWeb.callHandler("paymentFeedback", "2", new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {

                                    }
                                });
                            }
                        });
                        Toast.makeText(IntentOpenActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        Log.e("wangpan", payResult + "");
                    } else if (TextUtils.equals(resultStatus, "8000")) {
                        Toast.makeText(IntentOpenActivity.this, "???????????????...", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        Toast.makeText(IntentOpenActivity.this, "???????????????,????????????", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "5000")) {
                        Toast.makeText(IntentOpenActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(IntentOpenActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
//                case SDK_AUTH_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
//                    String resultStatus = authResult.getResultStatus();
//
//                    // ??????resultStatus ??????9000??????result_code
//                    // ??????200?????????????????????????????????????????????????????????????????????????????????
//                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//                        // ??????alipay_open_id???????????????????????????extern_token ???value
//                        // ??????????????????????????????????????????
//                        showAlert(PayDemoActivity.this, getString(R.string.auth_success) + authResult);
//                    } else {
//                        // ?????????????????????????????????
//                        showAlert(PayDemoActivity.this, getString(R.string.auth_failed) + authResult);
//                    }
//                    break;
//                }
                case AONMALY: {
                    SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                    SharedPreferences.Editor edit1 = sp1.edit();
                    edit1.putString("apply_url", Constant.text_url);
                    edit1.commit();
                    Intent intent = new Intent(IntentOpenActivity.this,MainActivity.class);
                    startActivity(intent);
                    break;
                }
                case ALAONMALY: {
                    Toast.makeText(IntentOpenActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    break;
                }
                case PcAONMALY: {
                    String Pcaonmaly = (String) msg.obj;
                    Toast.makeText(IntentOpenActivity.this, Pcaonmaly, Toast.LENGTH_SHORT).show();
                }
                break;
                default:
                    break;
            }
        }
    };
    private String token;
    private PayBean payBean;
    private String newUrl;
    private String purchaseOfEntry;
    private String appId;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private static final int AONMALY = 0; //???????????????code????????????
    private static final int ALAONMALY = 4; //?????????????????????
    private static final int PcAONMALY = 3; //????????????PC?????????????????????????????????
    private String TAG = "_TAG";
    //    // ??????????????????????????????????????????
    private long exitTime = 0;
    private String userId;
    private MyDialog dialog;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: pay start");
        setContentView(R.layout.activity_intent_open);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        purchaseOfEntry = intent.getStringExtra("PurchaseOfEntry");
        appId = intent.getStringExtra("appId");
        token = intent.getStringExtra("token");
        userId = intent.getStringExtra("userId");
        String orderNo = intent.getStringExtra("orderNo");
        String outTradeNo = intent.getStringExtra("outTradeNo");
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(orderNo) && !TextUtils.isEmpty(outTradeNo)) {
            String loca_url = Constant.locahost_url + "/" + userId + "/" + orderNo + "/" + outTradeNo;
            Log.e(TAG, "onCreate: " + loca_url);
            webView(loca_url);
        } else {
            webView(Constant.test_shoppingCart);
        }
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
        String userAgentString = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgentString + "; application-center");
        if (webSettings != null) {
            WebViewSetting.initweb(webSettings);
        }
        mIntentOpenPayWeb.loadUrl(url);
        //js??????????????????
        mIntentOpenPayWeb.addJavascriptInterface(new MJavaScriptInterface(getApplicationContext()), "ApplyFunc");
        wvClientSetting(mIntentOpenPayWeb);
        mIntentOpenPayWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mIntentOpenPayWeb != null && mIntentOpenPayWeb.canGoBack()) {
                        if (newUrl.contains("orderDetail/")) {
                            mIntentOpenPayWeb.goBack();
                        } else if (newUrl.contains("cashierDesk/")) {
                            if ((System.currentTimeMillis() - exitTime) > 2000) {
                                exitTime = System.currentTimeMillis();
                                showAlterDialog();
                            }
                        } else if (newUrl.contains("paymentSuccess/")) {
                            finish();
                        } else if (!userId.isEmpty()) {
                            if ((System.currentTimeMillis() - exitTime) > 2000) {
                                exitTime = System.currentTimeMillis();
                                showAlterDialog();
                            }
                        } else {
                            mIntentOpenPayWeb.goBack();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        /**
         * ????????????????????????
         */
        mIntentOpenPayWeb.registerHandler("getItemData", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (!purchaseOfEntry.isEmpty()) {
                    Log.e(TAG, "handler: 231231" + purchaseOfEntry);
                    function.onCallBack(purchaseOfEntry);
                } else {

                }
            }
        });
        /**
         * ????????????????????????
         */
        mIntentOpenPayWeb.registerHandler("getUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                String userInfo = sb.getString("userInfo", "");
                if (!userInfo.isEmpty()) {
                    function.onCallBack(userInfo);
                    Log.e(TAG, "handler: " + userInfo);
                } else {

                }
            }
        });

        mIntentOpenPayWeb.registerHandler("openPay", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e(TAG, "??????: "+data);
                if (!data.isEmpty()) {
                    Gson gson = new Gson();
                    payBean = gson.fromJson(data, PayBean.class);
                    Log.e(TAG, "openPay: " + data);
                    if (payBean.getPayType().equals("alipay")) { // ???????????????
                        alipaytypeOkhttp(payBean);
                    } else if (payBean.getPayType().equals("weixin")) { //????????????
                        boolean wxAppInstalled = isWxAppInstalled(IntentOpenActivity.this);
                        if (wxAppInstalled == true) {
                            Log.e(TAG, "openPay: " + wxAppInstalled);
                            wxpaytypeOkhttp(payBean);
                        } else {
                            Toast.makeText(IntentOpenActivity.this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        mIntentOpenPayWeb.registerHandler("getAppId", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (!appId.isEmpty()) {
                    Log.e(TAG, "handler: appid" + appId);
                    function.onCallBack(appId);
                }
            }
        });
        mIntentOpenPayWeb.registerHandler("goThirdApply", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                finish();
            }
        });

        mIntentOpenPayWeb.registerHandler("CashierDeskBack", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                showAlterDialog();
            }
        });
        /**
         * ????????????
         */
        mIntentOpenPayWeb.registerHandler("openCall", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Map map = JSONObject.parseObject(data, Map.class);
                String num = (String) map.get("num");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        /**
         * ????????????
         */
        mIntentOpenPayWeb.registerHandler("OpenPayIntent", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Map map = JSONObject.parseObject(data, Map.class);
                String tele = (String) map.get("tele");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tele));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    class MJavaScriptInterface {
        private Context context;

        public MJavaScriptInterface(Context context) {
            this.context = context;
        }

        //??????????????????/?????????
        @JavascriptInterface
        public void openPay(String data) {
            if (!data.isEmpty()) {
                Gson gson = new Gson();
                payBean = gson.fromJson(data, PayBean.class);
                Log.e(TAG, "openPay: " + data);
                if (payBean.getPayType().equals("alipay")) { // ???????????????
                    alipaytypeOkhttp(payBean);
                } else if (payBean.getPayType().equals("weixin")) { //????????????
                    boolean wxAppInstalled = isWxAppInstalled(IntentOpenActivity.this);
                    if (wxAppInstalled == true) {
                        Log.e(TAG, "openPay: " + wxAppInstalled);
                        wxpaytypeOkhttp(payBean);
                    } else {
                        Toast.makeText(context, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        //????????????  ???????????????
        @JavascriptInterface
        public void OpenPayIntent(String intentOpenPay) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + intentOpenPay));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        /**
         * ????????????/??????  ?????????????????????????????????????????????????????? ????????????
         */
        @JavascriptInterface
        public void closePay() {  //?????????  ????????????
            // ????????????
            Intent intent = new Intent();
            intent.setAction("action.refreshPay");
            intent.putExtra("paySuccessError", "chengong");
            sendBroadcast(intent);
            finish();
        }

        /**
         * ???????????????????????? ????????????
         */
        @JavascriptInterface
        public void goThirdApply() {
            Log.e(TAG, "handler: 123123123123123");
            finish();
        }

        /**
         * ??????????????????????????????
         */
        @JavascriptInterface
        public void CashierDeskBack() {
            Log.e(TAG, "onKey: 3333333");
            showAlterDialog();
        }
    }

    /**
     * ????????????????????????
     *
     * @param context
     * @return true ?????????   false ?????????
     */
    public static boolean isWxAppInstalled(Context context) {
        IWXAPI wxApi = WXAPIFactory.createWXAPI(context, null);
        wxApi.registerApp(Constant.APP_ID);
        boolean bIsWXAppInstalled = false;
        bIsWXAppInstalled = wxApi.isWXAppInstalled();
        return bIsWXAppInstalled;
    }

    private void alipaytypeOkhttp(final PayBean payBean) {
        Log.e(TAG, "?????????: "+token+"////"+payBean.getUserId()+"////"+payBean.getOutTradeNo()+"////"+payBean.getPayType());
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
                Log.e(TAG, "pay1 onResponse: " + string);
                Gson gson = new Gson();
                PayType payType = gson.fromJson(string, PayType.class);
                if (payType.getCode() == 200) {
                    alipayOkhttp(payBean);
                } else if(payType.getCode() == 401){
                    Message message = new Message();
                    message.what = AONMALY;
                    message.obj = payType.getMsg();
                    mHandler.sendMessage(message);
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
                } else if(payType.getCode() == 401){
                    Message message = new Message();
                    message.what = AONMALY;
                    message.obj = payType.getMsg();
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void alipayOkhttp(PayBean payBean) {
        String payUrl = Constant.appOrderInfo + payBean.getOutTradeNo();
        Log.i(TAG, "alipayOkhttp: "+payUrl);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(payUrl)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {

            private AppOrderInfo appOrderInfo;

            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    String string = response.body().string();
                    Log.e(TAG, "pay2 onResponse: " + string);
                    Gson gson = new Gson();
                    appOrderInfo = gson.fromJson(string, AppOrderInfo.class);
                    //orderInfo??????????????????????????????????????????url
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
                    // ??????????????????
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else {
                    Message message = new Message();
                    if (appOrderInfo !=null){
                        message.what = AONMALY;
                        message.obj = appOrderInfo.getMsg();
                    }else {//??????????????????
                        message.what = ALAONMALY;
                    }
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void wxPayOkhttp(PayBean payBean) {
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        // ??????app???????????????
        msgApi.registerApp(Constant.APP_ID);

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(Constant.wxPay_appOrderInfo + payBean.getOutTradeNo())
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {

            private WxPayBean wxPayBean;

            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    String string = response.body().string();
                    Log.e(TAG, "onResponse: " + string);
                    Gson gson = new Gson();
                    wxPayBean = gson.fromJson(string, WxPayBean.class);
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
                } else if (response.code() == 500) {
                    Message message = new Message();
                    message.what = PcAONMALY;
                    message.obj = "??????????????????????????????????????????????????????";
                    mHandler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = AONMALY;
                    message.obj = wxPayBean.getMsg();
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    /**
     * ?????????
     *
     * @param mIntentOpenPay
     */
    private void wvClientSetting(BridgeWebView mIntentOpenPay) {
        MyWebViewClient myWebViewClient = new MyWebViewClient(mIntentOpenPay, mWebError);
        mIntentOpenPay.setWebViewClient(myWebViewClient);
        myWebViewClient.setOnCityClickListener(new MyWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {
                newUrl = name;
                Log.e(TAG, "onCityClick: " + newUrl);
            }
        });
        MWebChromeClient mWebChromeClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError, mLoadingPage);
        mIntentOpenPay.setWebChromeClient(mWebChromeClient);
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
     * ?????????????????????????????????
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

    @Subscribe(threadMode = ThreadMode.MAIN) //???ui????????????
    public void onMessageEvent(String event) {
        if (event.equals("????????????")) {
            // ??????????????????????????????????????????????????????????????????????????????
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
                    mIntentOpenPayWeb.callHandler("paymentFeedback", "1", new CallBackFunction() {
                        @Override
                        public void onCallBack(String data) {

                        }
                    });
                }
            });
        } else if (event.equals("????????????")) {
            // ??????????????????????????????????????????????????????????????????????????????
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
                    mIntentOpenPayWeb.callHandler("paymentFeedback", "2", new CallBackFunction() {
                        @Override
                        public void onCallBack(String data) {

                        }
                    });
                }
            });
        }
        Toast.makeText(this, event, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void finish() {
//        if (userId != null) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                exitTime = System.currentTimeMillis();
//                showAlterDialog();
//            }
//        } else {
//            mIntentOpenPayWeb.goBack();
//        }
//        super.finish();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showAlterDialog();
//            if (userId != null) {
//                if ((System.currentTimeMillis() - exitTime) > 2000) {
//                    exitTime = System.currentTimeMillis();
//
//                }
//            } else {
//                mIntentOpenPayWeb.goBack();
//            }
        }
        return false;
    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Log.e(TAG, "onBackPressed: ??????");
//        if (!userId.isEmpty()) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                exitTime = System.currentTimeMillis();
//                showAlterDialog();
//            }
//        }
//    }

    private void showAlterDialog() {
        dialog = new MyDialog(IntentOpenActivity.this, R.style.mdialog,
                new MyDialog.OncloseListener() {
                    @Override
                    public void onClick(boolean confirm) {
                        if (confirm) {
                            Log.e(TAG, "onClick: ?????????");
                            finish();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
        dialog.show();
    }


    /**
     * ??????????????????
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
        if (dialog != null){
            dialog.dismiss();
        }
        super.onDestroy();
    }
}
