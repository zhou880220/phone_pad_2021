package com.example.honey_create_cloud.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONObject;
import com.example.honey_create_cloud.BuildConfig;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.MyApplication;
import com.example.honey_create_cloud.MyHandlerCallBack;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.BrowserBean;
import com.example.honey_create_cloud.bean.HeadPic;
import com.example.honey_create_cloud.bean.NotificationBean;
import com.example.honey_create_cloud.bean.PictureUpload;
import com.example.honey_create_cloud.bean.RabbitMQBean;
import com.example.honey_create_cloud.bean.ShareSdkBean;
import com.example.honey_create_cloud.bean.ShareSdkPackages;
import com.example.honey_create_cloud.bean.TokenIsOkBean;
import com.example.honey_create_cloud.bean.VersionInfo;
import com.example.honey_create_cloud.broadcast.NotificationClickReceiver;
import com.example.honey_create_cloud.file.CleanDataUtils;
import com.example.honey_create_cloud.http.UpdateAppHttpUtil;
import com.example.honey_create_cloud.pushmessage.HuaWeiPushHmsMessageService;
import com.example.honey_create_cloud.util.BaseUtils;
import com.example.honey_create_cloud.util.BitmapUtil;
import com.example.honey_create_cloud.util.FileUtil;
import com.example.honey_create_cloud.util.MarketTools;
import com.example.honey_create_cloud.util.QMUITouchableSpan;
import com.example.honey_create_cloud.util.SPUtils;
import com.example.honey_create_cloud.util.SystemUtil;
import com.example.honey_create_cloud.util.VersionUtils;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MyWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;
import com.huawei.hms.utils.StringUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.listener.ExceptionHandler;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xj.library.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Notification.DEFAULT_ALL;
import static com.example.honey_create_cloud.ui.ClipImageActivity.REQ_CLIP_AVATAR;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.NewWebProgressbar)
    ProgressBar mNewWebProgressbar;
    @InjectView(R.id.new_Web)
    BridgeWebView mNewWeb;
    @InjectView(R.id.web_error)
    View mWebError;
    @InjectView(R.id.closeLoginPage)
    ImageView mCloseLoginPage;
    @InjectView(R.id.text_policy_reminder)
    TextView mTextPolicyReminder;
    @InjectView(R.id.text_policy_reminder_back)
    RelativeLayout mTextPolicyReminderBack;


    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case OPLOAD_IMAGE:
                    Log.e(TAG, "handleMessage: " + msg.obj);
                    newName = (String) msg.obj;
                    OkHttpClient client1 = new OkHttpClient();
                    String post = "{" +
                            "userId:'" + userid1 + '\'' +
                            ", url:'" + newName + '\'' +
                            '}';
                    MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");
                    RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, post);
                    Request request = new Request.Builder()
                            .addHeader("Authorization", accessToken)
                            .url(Constant.headPic)
                            .post(requestBody)
                            .build();
                    client1.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String string = response.body().string();
                            Log.e(TAG, "onResponse: " + string);
                            Gson gson = new Gson();
                            HeadPic headPic = gson.fromJson(string, HeadPic.class);
                            if (headPic.getCode() == 200) {
                                String tete = "mytest";
                                mNewWeb.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNewWeb.evaluateJavascript("window.sdk.double(\"" + tete + "\")", new ValueCallback<String>() {
                                            @Override
                                            public void onReceiveValue(String value) {
                                            }
                                        });
                                    }
                                });
                                mNewWeb.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNewWeb.callHandler("double", "tete", new CallBackFunction() {
                                            @Override
                                            public void onCallBack(String data) {
                                            }
                                        });
                                    }
                                });
                            } else {

                            }
                        }
                    });
                    break;
                case NOTIFICATION_MESSAGE: {
                    String notificationMsg = (String) msg.obj;

                }
                break;
                default:
                    break;
            }
            return false;
        }
    });

    //??????????????????
    public static List<String> logList = new CopyOnWriteArrayList<String>();

    //????????????
    private static final int REQUEST_CAPTURE = 100;
    //????????????
    private static final int REQUEST_PICK = 101;
    private static final String TAG = "MainActivity_TAG";
    volatile int num = 0;
    //?????????????????????????????????
    private File tempFile;
    //??????
    private static final int NOT_NOTICE = 2;//???????????????????????????
    private String mVersionName = "";
    private String totalCacheSize = "";
    private String clearSize = "";
    private static final String[] PERMISSIONS_APPLICATION = { //??????????????????
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE};
    private static final int VIDEO_PERMISSIONS_CODE = 1;
    private MyHandlerCallBack.OnSendDataListener mOnSendDataListener;
    private String token1;
    private String userid;
    private String newName = "";
    private String accessToken;
    private String channel_id = "myChannelId";
    private String channel_name = "???????????????";
    private String description = "???????????????";
    NotificationManager notificationManager;
    private static final int SDK_PAY_FLAG = 1;  //????????????
    private static final int OPLOAD_IMAGE = 2;  //??????????????????
    private static final int NOTIFICATION_MESSAGE = 3;  //????????????
    private String usertoken1;
    private String userid1;
    private MWebChromeClient myChromeWebClient;
    private int badgeCount = 0;  //????????????
    private int NOTIFICATION_NUMBER = 0;  //??????????????????
    private boolean ChaceSize = true;
    private boolean pageReload = true;
    private String myOrder;
    private Channel channel;
    private String receiveMsg;
    private String PolicyAndReminder = "???????????????????????????????????????";
    private boolean isFirstCache;//????????????????????????
    private RabbitMQBean rabbitMQBean;
    private HashMap<String, String> hashMap = new HashMap<String, String>();
    private String zxIdTouTiao;
    private HuaWeiPushHmsMessageService huaWeiPushHmsMessageOnClick;
    private String imei = "";
    private String huaWeiToken;
    private String oppoToken;
    private boolean isPrepareFinish = false;
    private static final int WAIT_INTERVAL = 2000;
    private long exitTime = 0;
    private Context mContext;

    //??????
    private IWXAPI wxApi;
    public static Tencent mTencent;
    private ShareSdkBean shareSdkBean;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
//        getRabbitMQAddressOkhttp();//??????RabbitMq??????????????????


        createNotificationChannel();//Mq?????????
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        initPush(); //??????????????????
        webView(Constant.text_url);//????????????
        myRequetPermission(PERMISSIONS_APPLICATION);//????????????
        initVersionName();//?????????????????????
        Uri uri = getIntent().getData();
        Log.e(TAG, "onCreate uri: "+uri);
        if (uri != null) {
            String thirdId = uri.getQueryParameter("thirdId");
            String open = uri.getQueryParameter("open");
            Log.e(TAG, "huaweiUrl open: " + open);
            if (thirdId != null) {
                Intent intent = new Intent(this, NewsActivity.class);
                intent.putExtra("url", thirdId);
                startActivity(intent);
            }
            if (open != null) {
                Log.e(TAG, "huaweiUrl: " + uri);
                //test://zzy:8080/home?open=message&appid=2&appName=????????????????????????  ????????????????????????
                String huaWei = uri.getQueryParameter("appid");
                String appName = uri.getQueryParameter("appName");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("appid", huaWei);
                jsonObject.put("appName", appName);
                String s = jsonObject.toJSONString();
                Log.e(TAG, "onNewIntent: " + s);
                webView(Constant.APP_NOTICE_LIST);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //??????????????????
                        mNewWeb.callHandler("PushMessageIntent", s, new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {

                            }
                        });
                    }
                }, 1000);//2????????????Runnable??????run??????
            }
        }
        mTextPolicyReminder.setText(generateSp(PolicyAndReminder));
        mTextPolicyReminder.setMovementMethod(LinkMovementMethod.getInstance());
//        showClearCache(); //????????????????????????

        Intent intent1 = getIntent();
        String app_notice_list = intent1.getStringExtra("APP_NOTICE_LIST");
        if (app_notice_list != null) {
            if (app_notice_list.equals("??????")) { // ?????????????????????
                Log.e(TAG, "onCreate: " + app_notice_list);
                webView(Constant.MyNews);
            } else if (app_notice_list.equals("??????")) { //?????????????????????
                String xiaomiMessage = intent1.getStringExtra("pushContentMessage");
//                SharedPreferences xiaomiPref = getSharedPreferences("xiaomiPushMessage",MODE_PRIVATE);
//                SharedPreferences.Editor edit = xiaomiPref.edit();
//                edit.putString("??????????????????",xiaomiMessage);
//                edit.commit();
                Log.e(TAG, "xiaomipush:2 " + xiaomiMessage);
                webView(Constant.APP_NOTICE_LIST);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //??????????????????
                        mNewWeb.callHandler("PushMessageIntent", xiaomiMessage, new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {

                            }
                        });
                    }
                }, 1000);//2????????????Runnable??????run??????
            }
        }

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateApp();
            }
        }, 5000);
    }

    /**
     * ??????????????????
     */
    private void initPush() {
        if (android.os.Build.BRAND.toLowerCase().contains("xiaomi")) {
            //???????????????????????????
            MyApplication.setMainActivity(this);
            //????????????????????????
            MiPushClient.subscribe(this, "ALL", null);
        }
        if (android.os.Build.BRAND.toLowerCase().contains("vivo")) {
            //??????Vivo????????????
            PushClient.getInstance(this).setTopic("ALL", new IPushActionListener() {
                @Override
                public void onStateChanged(int state) {
                    if (state != 0) {
                        Log.e(TAG, "????????????????????????" + state);
                    } else {
                        Log.e(TAG, "????????????????????????");
                    }
                }
            });
        }
        //??????huawei????????????
        if (android.os.Build.BRAND.toLowerCase().contains("huawei")) {
            SharedPreferences huaWeiPushPref = getSharedPreferences("HuaWeiPushToken", MODE_PRIVATE);
            //???????????????????????????????????????????????????
            huaWeiToken = huaWeiPushPref.getString("huaWeiToken", "");
            if (!huaWeiToken.isEmpty()) {
                Log.e(TAG, "HuaweiPushRequest: 1" + huaWeiToken);
                PushTokenRelation("1", huaWeiToken, "2");
            } else {
                Log.e(TAG, "HuaweiPushRequest: 2" + "??????token");
            }

            HmsMessaging.getInstance(MainActivity.this)
                    .subscribe("ALL")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.e(TAG, "subscribe Complete");
                            } else {
                                Log.e(TAG, "subscribe failed: ret=" + task.getException().getMessage());
                            }
                        }
                    });
        }

        if (android.os.Build.BRAND.toLowerCase().contains("oppo")) {
            SharedPreferences oppoPushPref = getSharedPreferences("OppoPushToken", MODE_PRIVATE);
            oppoToken = oppoPushPref.getString("OppoToken", "");
            if (!oppoToken.isEmpty()) {
                Log.e(TAG, "oppoPushRequest: 1" + oppoToken);
                PushTokenRelation("1", oppoToken, "4");
            } else {
                Log.e(TAG, "oppoPushRequest: 2" + "??????token");
            }
        }
    }

    /**
     * ??????????????????????????????Token
     */
    private void getHuaweiToken() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                    Log.i(TAG, "get token:" + token);
                    if (!TextUtils.isEmpty(token)) {
                        sendRegTokenToServer(token);
                    }
                } catch (ApiException e) {
                    Log.e(TAG, "get token failed, " + e);
                }
            }
        }.start();
    }

    private void sendRegTokenToServer(String token) {
        Log.i(TAG, "sending token to server. token:" + token);
    }

    //????????????
    public void updateApp() {
        int sysVersion = VersionUtils.getVersion(this);
        Log.e(TAG, "updateApp: "+sysVersion);
        new UpdateAppManager
                .Builder()
                //??????Activity
                .setActivity(this)
                //????????????
                .setUpdateUrl(Constant.WEBVERSION + sysVersion)
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        Log.e(TAG, "updateApp Exception: "+e.getMessage());
                        e.printStackTrace();
                    }
                })
                //??????httpManager???????????????
                .setHttpManager(new UpdateAppHttpUtil())
                .setTopPic(R.mipmap.top_3)
                //????????????????????????????????????
                .setThemeColor(0xff47bbf1)
                .build()
                //????????????????????????????????????
                .update();
    }



    /**
     * ??????RabbitMQ????????????
     */
    private void getRabbitMQAddressOkhttp() {
        OkHttpClient client = new OkHttpClient();
        Request builder = new Request.Builder()
                .url(Constant.GETRabbitMQAddress)
                .get()
                .build();
        client.newCall(builder).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Gson gson = new Gson();
                rabbitMQBean = gson.fromJson(string, RabbitMQBean.class);
                Log.e(TAG, "getRabbitMQAddressOkhttp: " + string);
            }
        });
    }

    private void showClearCache() {
        SharedPreferences spCache = getSharedPreferences("ClearCache", MODE_PRIVATE);
        boolean firstCache = spCache.getBoolean("ClearCache", true);
        if (firstCache == true) {
            SharedPreferences.Editor edit = spCache.edit();
            edit.putBoolean("ClearCache", false);
            edit.commit();
        } else {
        }
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
                    Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
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
                    Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
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
     * ?????????webview js??????
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void webView(String url) {
        String hasUpdate = (String) SPUtils.getInstance().get(Constant.HAS_UDATE, "0");
        if (hasUpdate.equals("1")) {
            Log.e(TAG,"----> h5 page has update");
            mNewWeb.clearCache(true);
        }

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

        //Handler????????????????????????????????????????????????H5???????????????Native?????????????????????h5??????send()??????????????????????????????MyHandlerCallBack
        mNewWeb.setDefaultHandler(new MyHandlerCallBack(mOnSendDataListener));
        myChromeWebClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError);
        MyWebViewClient myWebViewClient = new MyWebViewClient(mNewWeb, mWebError);
        myWebViewClient.setOnCityClickListener(new MyWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {  //??????????????????????????????
                myOrder = name;
                Log.e(TAG, "onCityClick: "+name);
                if (name != null) {
                    if (name.equals(Constant.login_url)) {
                        mTextPolicyReminder.setVisibility(View.VISIBLE);
                        mCloseLoginPage.setVisibility(View.VISIBLE);
                        mTextPolicyReminderBack.setVisibility(View.VISIBLE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    } else if (name.equals(Constant.register_url)) {
                        mTextPolicyReminder.setVisibility(View.VISIBLE);
                        mCloseLoginPage.setVisibility(View.VISIBLE);
                        mTextPolicyReminderBack.setVisibility(View.GONE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    } else if (name.contains("bindPhone")) {
                        Log.e(TAG, "onCityClick: bind");
                        mTextPolicyReminder.setVisibility(View.VISIBLE);
                        mCloseLoginPage.setVisibility(View.VISIBLE);
                        mTextPolicyReminderBack.setVisibility(View.GONE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    }  else if (name.contains("/about")) {
                        mTextPolicyReminder.setVisibility(View.GONE);
                        mCloseLoginPage.setVisibility(View.GONE);
                        mTextPolicyReminderBack.setVisibility(View.GONE);
                    } else {
                        pageReload = true;
                        mTextPolicyReminder.setVisibility(View.GONE);
                        mCloseLoginPage.setVisibility(View.GONE);
                        mTextPolicyReminderBack.setVisibility(View.GONE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);  //SOFT_INPUT_ADJUST_RESIZE
                    }
                }
            }
        });
        mNewWeb.setWebViewClient(myWebViewClient);
        mNewWeb.setWebChromeClient(myChromeWebClient);
        mNewWeb.loadUrl(url);

        //????????????
        mNewWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.e(TAG, "onKey: web back  1");
                    if (mNewWeb != null && mNewWeb.canGoBack()) {
                        Log.e(TAG, "onKey: web back  2"+myOrder);
//                        SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
//                        String userInfo = sb.getString("userInfo", "");
                        if (myOrder.contains("/home")) { //???????????????????????????  ??????????????????
                            finish();
//                            mNewWeb.goBack();
                        } else if (myOrder.contains("/information")) { //????????????????????????????????????
                            webView(Constant.text_url);
                        } else {
                            mNewWeb.goBack();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        //?????????????????????????????????????????? ????????????
        mCloseLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mNewWeb.canGoBack()) {
                        webView(Constant.text_url);
                        mCloseLoginPage.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //js??????????????????
        mNewWeb.addJavascriptInterface(new MJavaScriptInterface(getApplicationContext()), "ApplyFunc");

        //??????????????????????????????Handler?????????  ???????????????
        mNewWeb.registerHandler("getVersionName", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!mVersionName.isEmpty()) {
//                        function.onCallBack("V" + mVersionName);
                        int sysVersion = VersionUtils.getVersion(mContext);
                        VersionInfo versionInfo = new VersionInfo(mVersionName, sysVersion);
                        function.onCallBack(new Gson().toJson(versionInfo));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        /**
         * ??????????????????
         */
        mNewWeb.registerHandler("setCookie", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        zxIdTouTiao = data;
                        Log.e(TAG, "setCookie: " + data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //???????????? ????????????????????? ????????????
        mNewWeb.registerHandler("getCache", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (ChaceSize == true) {
                        if (!totalCacheSize.isEmpty()) {
                            function.onCallBack(totalCacheSize);
                        }
                    } else {
                        function.onCallBack("0.00MB");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //??????????????????????????????
        mNewWeb.registerHandler("ClearCache", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    CleanDataUtils.clearAllCache(Objects.requireNonNull(MainActivity.this));
                    clearSize = CleanDataUtils.getTotalCacheSize(Objects.requireNonNull(MainActivity.this));
                    if (!clearSize.isEmpty()) {
                        ChaceSize = false;
                        function.onCallBack(clearSize);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //?????????????????????
        mNewWeb.registerHandler("getTakeCamera", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    //????????????
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        //??????READ_EXTERNAL_STORAGE??????
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                VIDEO_PERMISSIONS_CODE);
                    } else {
                        if (!data.isEmpty()) {
                            String replace1 = data.replace("\"", "");
                            String replace2 = replace1.replace("token:", "");
                            String replace3 = replace2.replace("{", "");
                            String replace4 = replace3.replace("}", "");
                            String[] s = replace4.split(" ");
                            token1 = s[0];
                            userid = s[1];
                            gotoCamera();
                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //???????????????????????????
        mNewWeb.registerHandler("getPhotoAlbum", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    //????????????
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        //??????READ_EXTERNAL_STORAGE??????
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                VIDEO_PERMISSIONS_CODE);
                    } else {
                        if (!data.isEmpty()) {
                            String replace1 = data.replace("\"", "");
                            String replace2 = replace1.replace("token:", "");
                            String replace3 = replace2.replace("{", "");
                            String replace4 = replace3.replace("}", "");
                            String[] s = replace4.split(" ");
                            token1 = s[0];
                            userid = s[1];
                            gotoPhoto();
                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //????????????????????????
        mNewWeb.registerHandler("getNotification", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    boolean enabled = isNotificationEnabled(MainActivity.this);
                    if (enabled == true) {
                        function.onCallBack("1");
                    } else {
                        function.onCallBack("2");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //???????????????????????????????????????
        mNewWeb.registerHandler("setUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    Log.e(TAG, "????????????????????????: " + data);
                    if (!data.isEmpty()) {
                        SharedPreferences sb = MainActivity.this.getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                        SharedPreferences.Editor edit = sb.edit();
                        edit.putString("userInfo", data);
                        edit.commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //???????????????????????????????????????
        mNewWeb.registerHandler("getUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                    String userInfo = sb.getString("userInfo", "");
                    Log.e("wangpan", userInfo);
                    if (!userInfo.isEmpty()) {
                        function.onCallBack(sb.getString("userInfo", ""));
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //??????????????????token userID
        mNewWeb.registerHandler("getToken", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Map map = JSONObject.parseObject(data, Map.class);
                        String usertoken = (String) map.get("token");
                        String userID = (String) map.get("userID");

                        Log.e(TAG, "??????????????????: " + usertoken + "---" + userID);
                        if (!usertoken.isEmpty()) {
                            usertoken1 = usertoken;
                            userid1 = userID;
                            if (android.os.Build.BRAND.toLowerCase().contains("huawei")) {
                                PushTokenRelation("2", huaWeiToken, "2");
                                getHuaweiToken();
                            }
                            if (android.os.Build.BRAND.toLowerCase().contains("xiaomi")) { //oK8vlaSJEtJwgL+Izrxq/7/PKCkfBXzKGScemsWX2wpLGCxl4Ky6T20zScPgox8K
                                //????????????????????????
                                MiPushClient.setAlias(MainActivity.this, userid1, null);
                                PushTokenRelation("4", userid1, "3");
                            }
                            if (android.os.Build.BRAND.toLowerCase().contains("vivo")) {
                                String vivoRegId = PushClient.getInstance(MainActivity.this).getRegId();
                                Log.e(TAG, "vivoRegId: " + vivoRegId);
                                PushTokenRelation("4", vivoRegId, "5");
                                //??????Vivo????????????
//                                PushClient.getInstance(MainActivity.this).bindAlias(userid1, new IPushActionListener() {
//                                    @Override
//                                    public void onStateChanged(int state) {
//                                        if (state != 0) {
//                                            Log.e(TAG, "??????????????????[ " + state + "]");
//                                        } else {
//                                            Log.e(TAG, "??????????????????");
//                                        }
//                                    }
//                                });
                            }
                            if (android.os.Build.BRAND.toLowerCase().contains("oppo")) {
                                PushTokenRelation("2", oppoToken, "4");
                            }

                            SharedPreferences sb = MainActivity.this.getSharedPreferences("NotificationUserId", MODE_PRIVATE);
                            SharedPreferences.Editor edit = sb.edit();
                            edit.putString("NotifyUserId", usertoken1);
                            edit.commit();

                            //??????userId????????????
                            String notifyUserId = sb.getString("NotifyUserId", "");
                            //deleteUserQueue(); //????????????
                            //??????????????????????????????7.0  ?????????????????????????????? ?????????????????????
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Log.e(TAG, "?????????????????????7.0");
                                if (!TextUtils.isEmpty(notifyUserId)) {
                                    notificationChange(userid1, "0");
//                                    new Thread(() -> basicConsume(myHandler)).start();
                                }
                            } else {
                                Log.e(TAG, "????????????????????????7.0");
                            }
                        }
                    } else {
                    }
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
                    Log.e(TAG, "???????????????:1 " + data);
                    if (!data.isEmpty()) {
                        Map map = JSONObject.parseObject(data, Map.class);
//                        String _redirectUrl = (String) map.get("redirectUrl");//"https://mobileclientthird.zhizaoyun.com/jsapi/view/api.html";//
//                        Map<String,String> reqMap = BaseUtils.urlSplit(_redirectUrl);
                        String redirectUrl = (String) map.get("redirectUrl");//"http://172.16.41.4:3001/equipment/app/home?access_token="+reqMap.get("access_token");//
                        String currentUrl = mNewWeb.getUrl();
                        Log.e(TAG, "currentUrl: "+currentUrl );
                        int appLyId = (int) map.get("appId");
                        int toDetail = map.get("toDetail") ==null ? 0 : (int) map.get("toDetail");
                        int fromDetail = map.get("fromDetail") ==null ? 0 : (int) map.get("fromDetail");
                        String appId = String.valueOf(appLyId);
                        if (!redirectUrl.isEmpty()) {
                            Log.e(TAG, "???????????????:2 " + redirectUrl);
                            Log.e(TAG, (toDetail == 1) +" toDetail: "+toDetail);
                            if (toDetail == 1) {//????????????????????????
                                Intent intent = new Intent(MainActivity.this, AppDetailActivity.class);
                                intent.putExtra("url", redirectUrl);
                                intent.putExtra("token", usertoken1);
                                intent.putExtra("userid", userid1);
                                intent.putExtra("appId", appId);
                                intent.putExtra("isFromHome", currentUrl.contains("apply") ? "0": "1");
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "to other: ");
                                if (zxIdTouTiao == null || zxIdTouTiao.isEmpty()) {
                                    Log.e(TAG, "???????????????:3 " + redirectUrl);
                                    Intent intent;
                                    //??????app????????????????????????
                                    if (redirectUrl.contains("equipment/app")) {
                                        intent = new Intent(MainActivity.this, EquipmentActivity.class);
                                    }else {
                                        intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
                                    }
                                    intent.putExtra("url", redirectUrl);
                                    intent.putExtra("token", usertoken1);
                                    intent.putExtra("userid", userid1);
                                    intent.putExtra("appId", appId);
                                    intent.putExtra("isFromHome", currentUrl.contains("apply") ? "0": "1");
                                    intent.putExtra("fromDetail", fromDetail+"");
                                    startActivity(intent);
                                } else {
                                    Log.e(TAG, "???????????????:4" + redirectUrl);
                                    Intent intent;
                                    //??????app????????????????????????
                                    if (redirectUrl.contains("equipment/app")) {
                                         intent = new Intent(MainActivity.this, EquipmentActivity.class);
                                    }else {
                                         intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
                                    }
                                    intent.putExtra("url", redirectUrl);
                                    intent.putExtra("token", usertoken1);
                                    intent.putExtra("userid", userid1);
                                    intent.putExtra("appId", appId);
                                    intent.putExtra("zxIdTouTiao", zxIdTouTiao);
                                    intent.putExtra("isFromHome", currentUrl.contains("apply") ? "0": "1");
                                    intent.putExtra("fromDetail", fromDetail+"");
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

        //????????????????????????   ???????????????????????????
        mNewWeb.registerHandler("NewNotifiction", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    gotoSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //????????????
        mNewWeb.registerHandler("openNotification", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    gotoSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //????????????????????????????????????
        mNewWeb.registerHandler("showNewsParams", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e(TAG, "??????????????????: " + data);
                try {
                    if (!data.isEmpty()) {
                        Map map = JSONObject.parseObject(data, Map.class);
                        String link = (String) map.get("link");
//                        String code = (String) map.get("code");
//                        String token = (String) map.get("token");
                        String from = (String) map.get("from");
                        if (!data.isEmpty()) {
                            Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                            intent.putExtra("url", link);
                            intent.putExtra("token", token1);
                            intent.putExtra("from", from);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //??????????????????????????????????????????????????????
        mNewWeb.registerHandler("CashierDeskGo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Log.e(TAG, "??????????????????: " + data);
                        Map map = JSONObject.parseObject(data, Map.class);
                        String userId = (String) map.get("userId");
                        String orderNo = (String) map.get("orderNo");
                        String outTradeNo = (String) map.get("outTradeNo");

                        pageReload = true;
                        Intent intent = new Intent(MainActivity.this, IntentOpenActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("orderNo", orderNo);
                        intent.putExtra("outTradeNo", outTradeNo);
                        intent.putExtra("token", usertoken1);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //????????????????????????????????????????????????  ???????????????????????????????????????
//        mNewWeb.registerHandler("OpenPayIntent", new BridgeHandler() {
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                try {
//                    if (!data.isEmpty()) {
//                        Log.e(TAG, "???????????????: " + data);
//                        Map map = JSONObject.parseObject(data, Map.class);
//                        String tele = (String) map.get("tele");
//                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tele));
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        //????????????????????????????????????????????????  ????????????????????????????????????
        mNewWeb.registerHandler("openCall", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Map map = JSONObject.parseObject(data, Map.class);
                        String num = (String) map.get("num");
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //?????????????????? ????????????????????????
        mNewWeb.registerHandler("ClearUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {


                try {
                    if (Build.BRAND.toLowerCase().contains("oppo")) {
                        PushTokenRelation("3", userid1, "4");
                    }
                    if (android.os.Build.BRAND.toLowerCase().contains("xiaomi")) {
                        //??????????????????????????????????????????
                        MiPushClient.unsetAlias(MainActivity.this, userid1, null);
                        PushTokenRelation("3", userid1, "3");
                    }
                    if (android.os.Build.BRAND.toLowerCase().contains("vivo")) {
                        //????????????????????????Vivo????????????
                        PushClient.getInstance(MainActivity.this).unBindAlias(userid1, new IPushActionListener() {
                            @Override
                            public void onStateChanged(int state) {
                                if (state != 0) {
                                    PushTokenRelation("3", userid1, "5");
                                    Log.e(TAG, "??????????????????" + state);
                                } else {
                                    Log.e(TAG, "??????????????????");
                                }
                            }
                        });
                    }
                    if (android.os.Build.BRAND.toLowerCase().contains("huawei")) {
                        PushTokenRelation("3", userid1, "2");
                        //??????????????????????????????????????????
                        HmsMessaging.getInstance(MainActivity.this)
                                .unsubscribe(userid1)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.e(TAG, "unsubscribe Complete");
                                        } else {
                                            Log.e(TAG, "unsubscribe failed: ret=" + task.getException().getMessage());
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    SharedPreferences sb = MainActivity.this.getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                    SharedPreferences.Editor edit = sb.edit();
                    edit.clear();
                    edit.commit();

                    SharedPreferences sb1 = MainActivity.this.getSharedPreferences("NotificationUserId", MODE_PRIVATE);
                    SharedPreferences.Editor edit1 = sb1.edit();
                    edit1.clear();
                    edit1.commit();

                    SharedPreferences sb2 = MainActivity.this.getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                    SharedPreferences.Editor edit2 = sb2.edit();
                    edit2.clear();
                    edit2.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //?????????????????????????????????????????????
        mNewWeb.registerHandler("intentBrowser", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Map map = JSONObject.parseObject(data, Map.class);
                        String Url = (String) map.get("url");
                        Gson gson = new Gson();
                        BrowserBean browserBean = gson.fromJson(Url, BrowserBean.class);
                        if (!Url.isEmpty()) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(browserBean.getUrl());
                            intent.setData(content_url);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //??????????????????????????????????????????????????????  ??????????????????????????????
        mNewWeb.registerHandler("versionUpdate", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Log.e(TAG, "handler: " + data);
                        Map map = JSONObject.parseObject(data, Map.class);
                        int type = (int) map.get("type");
                        if (type == 0) {
                            CleanDataUtils.clearAllCache(Objects.requireNonNull(MainActivity.this));
                            mNewWeb.clearCache(true);
                            mNewWeb.clearHistory();
                        } else {
                            MarketTools.apphomelist(MainActivity.this);
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
                        wxApi = WXAPIFactory.createWXAPI(MainActivity.this, Constant.APP_ID);
                        wxApi.registerApp(Constant.APP_ID);
                        //QQ?????????
                        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, MainActivity.this);

//                        Map map = new Gson().fromJson(data, Map.class);// JSONObject.parseObject(num, Map.class);
//                        String num = (String) map.get("obj");
//                        Log.e(TAG, "type: " + num);
//                        Map mapType = new Gson().fromJson(num, Map.class);
//                        int type = (int) mapType.get("type");
//                        Log.e(TAG, "type: " + type);
//                        String value = String.valueOf(mapType.get("data"));
//                        Gson gson = new Gson();
//                        ShareSdkBean shareSdkBean = gson.fromJson(value, ShareSdkBean.class);

                        ShareSdkPackages shareSdkPackages = new Gson().fromJson(data, ShareSdkPackages.class);
                        int type = shareSdkPackages.getType();
                        Log.e(TAG, "type: " + type);
                        ShareSdkBean shareSdkBean = shareSdkPackages.getData();
                        Log.e(TAG, "url: " + shareSdkBean.getUrl());
                        if (type == 1) {
                            boolean wxAppInstalled = isWxAppInstalled(MainActivity.this);
                            if (wxAppInstalled == true) {
                                isShareSuc = true;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        wechatShare(0, shareSdkBean); //??????
                                    }
                                }).start();
                            } else {
                                Toast.makeText(MainActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                            }
                        } else if (type == 2) {
                            boolean wxAppInstalled1 = isWxAppInstalled(MainActivity.this);
                            if (wxAppInstalled1 == true) {
                                isShareSuc = true;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        wechatShare(1, shareSdkBean); //?????????
                                    }
                                }).start();
                            } else {
                                Toast.makeText(MainActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                            }
                        } else if (type == 3) {
                            boolean qqClientAvailable = isQQClientAvailable(MainActivity.this);
                            if (qqClientAvailable == true) {
                                isShareSuc = true;
                                qqFriend(shareSdkBean);
                            } else {
                                Toast.makeText(MainActivity.this, "???????????????QQ", Toast.LENGTH_SHORT).show();
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

    /**
     * accessEquipment ???????????? (1???iphone???2???huawei???3???mi???4???oppo???5???vivo)
     */
    private void PushTokenRelation(String PushFuncType, String UserPushToken, String accessEquipmentType) {
        Log.e(TAG, "PushTokenRelation: " + UserPushToken);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            imei = getId();
        } else {
            imei = SystemUtil.getUniqueIdentificationCode(MainActivity.this);
        }
        Log.e(TAG, "PushTokenRelation: " + PushFuncType + "_" + UserPushToken + "_" + accessEquipmentType);

        if (PushFuncType.equals("1")) {
            String PushHuaweiBody = "{" +
                    "accessEquipment:'" + accessEquipmentType + '\'' +
                    ", equipmentType:'" + "3" + '\'' +
                    ", equipmentIdCode:'" + imei + '\'' +
                    ", status:'" + "0" + '\'' +
                    ", token:'" + UserPushToken + '\'' +
                    ", userId:'" + "" + '\'' +
                    '}';

            MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");
            RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, PushHuaweiBody);

            OkHttpClient HuaweiPushClient = new OkHttpClient();
            Request HuaweiRequest = new Request.Builder()
                    .url(Constant.userPushRelation)
                    .post(requestBody)
                    .build();
            HuaweiPushClient.newCall(HuaweiRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String string = response.body().string();
                    Log.e(TAG, "HuaweiPushRequest:1 " + string);
                }
            });
        } else if (PushFuncType.equals("2")) {
            Log.e(TAG, "PushTokenRelation:userid1: " + userid1);
            String PushHuaweiBody = "{" +
                    "accessEquipment:'" + accessEquipmentType + '\'' +
                    ", equipmentType:'" + "3" + '\'' +
                    ", equipmentIdCode:'" + imei + '\'' +
                    ", status:'" + "0" + '\'' +
                    ", token:'" + UserPushToken + '\'' +
                    ", userId:'" + userid1 + '\'' +
                    '}';
            Log.e(TAG, "PushTokenRelation: " + accessEquipmentType + " imei:" + imei + " pushToken:" + UserPushToken + " userId:" + userid1 + "");
            MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");
            RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, PushHuaweiBody);

            OkHttpClient HuaweiPushClient = new OkHttpClient();
            Request HuaweiRequest = new Request.Builder()
                    .url(Constant.userFirstUpdate)
                    .post(requestBody)
                    .build();
            HuaweiPushClient.newCall(HuaweiRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String string = response.body().string();
                    Log.e(TAG, "HuaweiPushRequest:2 " + string);
                }
            });
        } else if (PushFuncType.equals("3")) {
            String PushHuaweiBody = "{" +
                    "accessEquipment:'" + accessEquipmentType + '\'' +
                    ", equipmentType:'" + "3" + '\'' +
                    ", equipmentIdCode:'" + imei + '\'' +
                    ", status:'" + "0" + '\'' +
                    ", token:'" + UserPushToken + '\'' +
                    ", userId:'" + userid1 + '\'' +
                    '}';

            MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");
            RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, PushHuaweiBody);

            OkHttpClient HuaweiPushClient = new OkHttpClient();
            Request HuaweiRequest = new Request.Builder()
                    .url(Constant.userPushRelationUpdate)
                    .post(requestBody)
                    .build();
            HuaweiPushClient.newCall(HuaweiRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String string = response.body().string();
                    Log.e(TAG, "HuaweiPushRequest:3 " + string);
                }
            });
        } else if (PushFuncType.equals("4")) {
            String PushHuaweiBody = "{" +
                    "accessEquipment:'" + accessEquipmentType + '\'' +
                    ", equipmentType:'" + "3" + '\'' +
                    ", equipmentIdCode:'" + imei + '\'' +
                    ", status:'" + "0" + '\'' +
                    ", token:'" + UserPushToken + '\'' +
                    ", userId:'" + userid1 + '\'' +
                    '}';

            MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");
            RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, PushHuaweiBody);

            OkHttpClient HuaweiPushClient = new OkHttpClient();
            Request HuaweiRequest = new Request.Builder()
                    .url(Constant.userPushRelation)
                    .post(requestBody)
                    .build();
            HuaweiPushClient.newCall(HuaweiRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String string = response.body().string();
                    Log.e(TAG, "HuaweiPushRequest:4 " + string);
                }
            });

        }
    }


    /**
     * Android???js??????   ????????????
     */
    class MJavaScriptInterface {
        private Context context;


        public MJavaScriptInterface(Context context) {
            this.context = context;
        }

        /**
         * ????????????????????????,????????????????????????
         */
        @JavascriptInterface
        public void setUserInfo(String userInfo) {
            Log.e("wangpan", userInfo);
            if (!userInfo.isEmpty()) {
                SharedPreferences sb = context.getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                SharedPreferences.Editor edit = sb.edit();
                edit.putString("userInfo", userInfo);
                edit.commit();
            }
        }

        //????????????  ???????????????
//        @JavascriptInterface
//        public void OpenPayIntent(String intentOpenPay) {
//            Log.e(TAG, "OpenPayIntent: " + intentOpenPay);
//            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + intentOpenPay));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }

        /**
         * ???????????????url??????
         *
         * @param interfaceUrl
         * @param appId
         */
//        @JavascriptInterface
//        public void showApplyParams(String interfaceUrl, String appId) {
//            if (!interfaceUrl.isEmpty()) {
//                Intent intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
//                intent.putExtra("url", interfaceUrl);
//                intent.putExtra("token", usertoken1);
//                intent.putExtra("userid", userid1);
//                intent.putExtra("appId", appId);
//                startActivity(intent);
//            } else {
//                Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
//            }
//        }

        /**
         * ??????????????????url
         */
        @JavascriptInterface
        public void showNewsParams(String addressUrl, String appId, String token) {
            if (!addressUrl.isEmpty()) {
                Log.e(TAG, "showNewsParams: " + addressUrl);
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("url", addressUrl);
                intent.putExtra("token", token1);
                startActivity(intent);
            } else {
                Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * ??????????????????????????????
         */
        @JavascriptInterface
        public void NewNotifiction() {
            gotoSet();
        }

        /**
         * ??????????????????token  ?????????????????????????????????????????????
         */
        @JavascriptInterface
        public void getToken(String usertoken, String userid) {

            if (!usertoken.isEmpty()) {
                usertoken1 = usertoken;
                userid1 = userid;

                SharedPreferences sb = context.getSharedPreferences("NotificationUserId", MODE_PRIVATE);
                SharedPreferences.Editor edit = sb.edit();
                edit.putString("NotifyUserId", usertoken1);
                edit.commit();

                //??????userId????????????
                String notifyUserId = sb.getString("NotifyUserId", "");
                //deleteUserQueue(); //????????????
                //??????????????????????????????7.0  ?????????????????????????????? ?????????????????????
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.e(TAG, "?????????????????????7.0");
                    if (!TextUtils.isEmpty(notifyUserId)) {
                        notificationChange(userid1, "0");
//                        new Thread(() -> basicConsume(myHandler)).start();
                    }
                } else {
                    Log.e(TAG, "????????????????????????7.0");
                }
            }
        }


        /**
         * ????????????????????????
         */
        @JavascriptInterface
        public void ClearUserInfo() {
            SharedPreferences sb = context.getSharedPreferences("userInfoSafe", MODE_PRIVATE);
            SharedPreferences.Editor edit = sb.edit();
            edit.clear();
            edit.commit();

            SharedPreferences sb1 = context.getSharedPreferences("NotificationUserId", MODE_PRIVATE);
            SharedPreferences.Editor edit1 = sb1.edit();
            edit1.clear();
            edit1.commit();

            SharedPreferences sb2 = context.getSharedPreferences("userInfoSafe", MODE_PRIVATE);
            SharedPreferences.Editor edit2 = sb2.edit();
            edit2.clear();
            edit2.commit();
        }

        @JavascriptInterface
        public void intentBrowser(String browser) {
            Gson gson = new Gson();
            BrowserBean browserBean = gson.fromJson(browser, BrowserBean.class);
            if (!browser.isEmpty()) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(browserBean.getUrl());
                intent.setData(content_url);
                startActivity(intent);
            }
        }

        /**
         * ????????????????????????
         */
        @JavascriptInterface
        public void goThirdApply() {
            mNewWeb.post(new Runnable() {
                @Override
                public void run() {
                    mNewWeb.evaluateJavascript("window.sdk.gotoMyOrder()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
            });
        }

        @JavascriptInterface
        public void CashierDeskGo(String userId, String orderNo, String outTradeNo) {
            pageReload = true;
            Log.e(TAG, "CashierDeskGo: " + userId + "--" + orderNo + "--" + outTradeNo + "--" + usertoken1);
            Intent intent = new Intent(MainActivity.this, IntentOpenActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("orderNo", orderNo);
            intent.putExtra("outTradeNo", outTradeNo);
            intent.putExtra("token", usertoken1);
            startActivity(intent);
        }
    }

    private void deleteUserQueue() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.DELETE_QUEUE + userid1)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    private void createNotificationChannel() {
        //Android8.0(API26)?????????????????????????????????????????????????????????????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channel_id, channel_name, importance);
            channel.setDescription(description);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        } else {
            notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //???ui????????????
    public void onMessageEvent(String event) {
        if (event.equals("????????????")) {
            // ??????????????????????????????????????????????????????????????????????????????
            mNewWeb.post(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    mNewWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + "1" + "\")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                        }
                    });
                }
            });
        } else if (event.equals("????????????")) {
            // ??????????????????????????????????????????????????????????????????????????????
            mNewWeb.post(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    mNewWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + "2" + "\")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                        }
                    });
                }
            });
        } else if (event.equals("????????????")) {
            webView(Constant.apply_url);
        } else if (event.equals("????????????")) {
            webView(Constant.text_url);
        }
    }

    /**
     * ?????????????????????????????????????????????
     */
    private void basicConsume(final Handler handler) {
        Log.e(TAG, "11?????????????????????7.0");
        try {
            //??????
            Connection connection = getConnection();
            if (connection != null) {
                //??????
                channel = connection.createChannel();
                // AMQP.Queue.DeclareOk declareOk = channel.queueDeclare("app.notice.queue." + "ASDFWERDFDFGDFGHFHFGHFDGHTY", true, false, false, null);  ?????????????????????
                Consumer consumer = new DefaultConsumer(channel) {

                    // ?????????????????????
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException {
                        super.handleDelivery(consumerTag, envelope, properties, body);
                        receiveMsg = new String(body, "UTF-8");
                        Log.e(TAG, "??????????????????: " + receiveMsg);
                        NotificationConsune();
                    }
                };
                channel.basicConsume("app.notice.queue." + userid1, true, "administrator", consumer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     */
    private Connection getConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQBean.getData().getMqAddress());//???????????????192.168.1.105
        int MqPort = Integer.parseInt(rabbitMQBean.getData().getMqPort());
        factory.setPort(MqPort);// ?????????:5672
        factory.setUsername(rabbitMQBean.getData().getMqUser());// ?????????
        factory.setPassword(rabbitMQBean.getData().getMqPassword());// ??????
        factory.setVirtualHost(rabbitMQBean.getData().getMqVirtualHost());
        try {
            return factory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void NotificationConsune() {
        OkHttpClient client1 = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.TOKEN_IS_OK + usertoken1)
                .get()
                .build();

        client1.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Gson gson = new Gson();
                TokenIsOkBean tokenIsOkBean = gson.fromJson(string, TokenIsOkBean.class);
                if (tokenIsOkBean.getCode() == 200) {
                    if (receiveMsg != null) {
                        sendNotification();
                    } else {
                    }
                }
            }
        });
    }

    private void sendNotification() {
        Gson gson1 = new Gson();
        Intent intent = new Intent(this, NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        Log.e(TAG, "sendNotification: " + receiveMsg);
        NotificationBean notificationBean = gson1.fromJson(receiveMsg, NotificationBean.class);
        SharedPreferences sb1 = getSharedPreferences("NotificationUserId", MODE_PRIVATE);
        String notifyToken = sb1.getString("NotifyUserId", "");
        if (!TextUtils.isEmpty(notifyToken) && !TextUtils.isEmpty(notificationBean.getUserId())
                && userid1.equals(notificationBean.getUserId()) && !notificationBean.getTitle().equals("??????????????????")) {
//            try {
            ShortcutBadger.applyCount(MainActivity.this, badgeCount++); //for 1.1.4+
            Log.e(TAG, "sendNotification: " + badgeCount);
            Notification notification = new NotificationCompat.Builder(MainActivity.this, channel_id)
                    .setContentTitle(notificationBean.getTitle())
                    .setContentText(notificationBean.getContent())
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo)
                    .setDefaults(DEFAULT_ALL)
                    .setColor(Color.parseColor("#5cabfa"))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setNumber(badgeCount)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
            notificationManager.notify(NOTIFICATION_NUMBER, notification);
            mNewWeb.post(new Runnable() {
                @Override
                public void run() {
                    mNewWeb.evaluateJavascript("window.sdk.noticeTimes(\"" + badgeCount + "\")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });

                    mNewWeb.callHandler("noticeTimes", badgeCount + "", new CallBackFunction() {
                        @Override
                        public void onCallBack(String data) {

                        }
                    });
                }
            });
        } else {
            //????????????
        }
    }


    /**
     * ??????????????????
     */
    private void gotoCamera() {
        //	???????????????????????????
        File dPictures = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //????????????
        String mFileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        //????????????
        String mFilePath = dPictures.getAbsolutePath() + "/" + mFileName;
        //?????????????????????????????????
//        tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"), System.currentTimeMillis() + ".jpg");
        tempFile = new File(mFilePath);
        //???????????????????????????
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //??????7.0???????????????????????????????????????xml/file_paths.xml
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * ???????????????
     */
    private void gotoPhoto() {
        //???????????????????????????
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "???????????????"), REQUEST_PICK);
    }

    /**
     * ???????????????
     */
//    private void mLodingTime() {
//        final AnimationView hideAnimation = new AnimationView();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hideAnimation.getHideAnimation(mLoadingPage, 500);
//                mLoadingPage.setVisibility(View.GONE);
//            }
//        }, 3000);
//    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        notificationManager.cancel(NOTIFICATION_NUMBER);
        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
            }
        });
        super.onResume();
        Log.e(TAG, "onResume: 111" );
//        refreshLogInfo();
    }

    private void notificationChange(String userId, String openStatus) {
        OkHttpClient client = new OkHttpClient();
//        String post = "{" +
//                "userId:'" + userId + '\'' +
//                ", openStatus:'" + openStatus + '\'' +
//                '}';
        String post1 = "{\n" +
                "    \"userId\":\"" + userId + "\",\n" +
                "    \"openStatus\":" + openStatus + "\n" +
                "}";

        MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, post1);
        final Request request = new Request.Builder()
                .url(Constant.NOTICE_OPEN_SWITCH)
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        badgeCount = 0;
        notificationManager.cancel(NOTIFICATION_NUMBER);
        ShortcutBadger.removeCount(this);
        SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
        String apply_url = sp1.getString("apply_url", "");//???????????????????????????????????????????????????
        Log.e(TAG, " onStart: "+ apply_url);
        if (!TextUtils.isEmpty(apply_url)) {
            webView(apply_url);
        }
        SharedPreferences.Editor edit = sp1.edit();
        edit.clear();
        edit.commit();

        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
            }
        });
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestart() {
        badgeCount = 0;
//        mNewWeb.reload(); //????????????????????????????????????????????????
//        ShortcutBadger.applyCount(this, badgeCount);
        ShortcutBadger.removeCount(this);
        notificationManager.cancel(NOTIFICATION_NUMBER);
        boolean notificationEnabled = isNotificationEnabled(this);
        if (notificationEnabled == true && usertoken1 != null) {
            notificationChange(userid1, "0");
        } else {

        }
        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {

            }
        });
        mNewWeb.evaluateJavascript("window.sdk.getOrderNotice()", new ValueCallback<String>() {  //?????????????????????????????????????????????
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onCallBack: ???????????????");
            }
        });
        mNewWeb.callHandler("getOrderNotice", "", new CallBackFunction() {  //?????????????????????????????????????????????
            @Override
            public void onCallBack(String data) {
                Log.e(TAG, "onCallBack: ???????????????");

            }
        });
        SharedPreferences sb = getSharedPreferences("NotificationUserId", MODE_PRIVATE);
        String notifyUserId = sb.getString("NotifyUserId", "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e(TAG, "?????????????????????7.0");
            if (!TextUtils.isEmpty(notifyUserId)) {
//                new Thread(() -> basicConsume(myHandler)).start();
            }
        } else {
            Log.e(TAG, "????????????????????????7.0");
        }
        super.onRestart();
    }

    @Override
    protected void onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e(TAG, "?????????????????????7.0");
//            new Thread(() -> basicConsume(myHandler)).start();
        } else {
            Log.e(TAG, "????????????????????????7.0");
        }
        super.onStop();
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

    /**
     * ?????????????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initVersionName() {
        totalCacheSize = CleanDataUtils.getTotalCacheSize(Objects.requireNonNull(MainActivity.this));
        try {
            //??????????????????
            PackageManager packageManager = getPackageManager();
            //?????????????????????
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //???????????????
            mVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????
     */
    private void myRequetPermission(String[] permissionsApplication) {
        // ???API?????? 23 ???????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(MainActivity.this, permissionsApplication, VIDEO_PERMISSIONS_CODE);
        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, PERMISSIONS_APPLICATION, 1);
//        } else {
//            mLodingTime();
//        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case VIDEO_PERMISSIONS_CODE:
                //??????????????????
                if (grantResults.length == PERMISSIONS_APPLICATION.length) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            //????????????????????????????????????
//                            showDialog();
                            Toast.makeText(MainActivity.this, "?????????????????????", Toast.LENGTH_LONG).show();
                            break;
                        } else {
                        }
                    }
                }
                break;
        }
    }

    //???????????????
    private void showDialog() {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("??????")
                .setMessage("??????????????????????????????????????????????????????????????????")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        goToAppSetting();
                    }
                })
                .setCancelable(false)
                .show();
    }

    // ????????????????????????????????????
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /**
     * ??????????????????
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        ClipImageActivity.goToClipActivity(this, uri);
    }

    /**
     * ??????????????????
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 10 && resultCode == 2) {//????????????????????????????????????????????????
            String apply_url = intent.getStringExtra("apply_url");//data:???????????????putExtra()??????????????????
            webView(apply_url);
        }
        switch (requestCode) {
            case NOT_NOTICE:
                myRequetPermission(PERMISSIONS_APPLICATION);//????????????????????????????????????????????????????????????
                break;
            case REQUEST_CAPTURE: //????????????????????????
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //????????????????????????
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    String realPathFromUri = BaseUtils.getRealPathFromURI(this, uri);
                    if (realPathFromUri.endsWith(".jpg") || realPathFromUri.endsWith(".png") || realPathFromUri.endsWith(".jpeg")) {
                        gotoClipActivity(uri);
                    } else {
                        Toast.makeText(this, "?????????????????????,???????????????", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case REQ_CLIP_AVATAR:  //??????????????????
                if (resultCode == RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = FileUtil.getRealFilePathFromUri(getApplicationContext(), uri);
                    long fileSize = FileUtil.getFileSize(cropImagePath);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String date = simpleDateFormat.format(new Date());
                    FileUtil.saveBitmapToSDCard(bitMap, "123");
                    //?????????????????????bitMap?????????????????????????????????

                    accessToken = "Bearer" + " " + token1;
                    OkHttpClient client = new OkHttpClient();//??????okhttpClient
                    //??????body??????????????????
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    File file = new File(cropImagePath);

                    final MediaType mediaType = MediaType.parse("image/jpeg");//??????????????????
                    builder.addFormDataPart("fileObjs", file.getName(), RequestBody.create(mediaType, file));
                    builder.addFormDataPart("fileNames", "");
                    builder.addFormDataPart("bucketName", Constant.bucket_Name);
                    builder.addFormDataPart("folderName", "headPic");
                    MultipartBody requestBody = builder.build();
                    final Request request = new Request.Builder()
                            .url(Constant.upload_multifile)
                            .addHeader("Authorization", accessToken)
                            .post(requestBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String s = response.body().string();
                            Log.e(TAG, "onResponse: " + s);
                            Gson gson = new Gson();
                            PictureUpload pictureUpload = gson.fromJson(s, PictureUpload.class);
                            if (pictureUpload.getCode() == 200) {
                                List<PictureUpload.DataBean> data = pictureUpload.getData();
                                Message message = myHandler.obtainMessage();
                                message.obj = data.get(0).getNewName();
                                message.what = OPLOAD_IMAGE;
                                myHandler.sendMessage(message);
                            } else {

                            }
                        }
                    });
                }
                break;
        }
    }


    /**
     * ??????????????????
     */
    private void gotoSet() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0??????
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", this.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", this.getPackageName());
            intent.putExtra("app_uid", this.getApplicationInfo().uid);
        } else {
            // ??????
//            getContext().getApplicationContext().getPackageName();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", this.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: "+isPrepareFinish);
        if(!isPrepareFinish){
            new Handler().postDelayed(
                    () -> isPrepareFinish = false,WAIT_INTERVAL
            );
            isPrepareFinish = true;
            Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = getIntent().getData(); //????????????????????????  ?????????????????????  oppo????????????
        if (uri != null) {
            String thirdId = uri.getQueryParameter("thirdId");
            if (thirdId != null) {
                intent = new Intent(this, NewsActivity.class);
                intent.putExtra("url", thirdId);
                startActivity(intent);
            }
            String open = uri.getQueryParameter("open");
            if (open.equals("message")) {
                Log.e(TAG, "huaweiUrl: " + uri);
                //test://zzy:8080/home?open=message&appid=2&appName=????????????????????????  ????????????????????????
                String huaWei = uri.getQueryParameter("appid");
                String appName = uri.getQueryParameter("appName");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("appid", huaWei);
                jsonObject.put("appName", appName);
                String s = jsonObject.toJSONString();
                Log.e(TAG, "onNewIntent: " + s);
                webView(Constant.APP_NOTICE_LIST);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //??????????????????
                        mNewWeb.callHandler("PushMessageIntent", s, new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {

                            }
                        });
                    }
                }, 1000);//2????????????Runnable??????run??????
            }
        }

        String app_notice_list = intent.getStringExtra("APP_NOTICE_LIST");
        String xiaomiMessage = intent.getStringExtra("pushContentMessage");
        if (app_notice_list != null) {
//            webView(Constant.APP_NOTICE_LIST);
            if (app_notice_list.equals("??????")) { //?????????????????????
                webView(Constant.MyNews);
            } else if (app_notice_list.equals("??????")) {
                webView(Constant.APP_NOTICE_LIST);
                Log.e(TAG, "xiaomiMessage: " + xiaomiMessage);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //??????????????????
                        mNewWeb.callHandler("PushMessageIntent", xiaomiMessage, new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {

                            }
                        });
                    }
                }, 1000);//2????????????Runnable??????run??????
            }
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param context
     * @return
     */
    private boolean isNotificationEnabled(Context context) {
        boolean isOpened = false;
        try {
            isOpened = NotificationManagerCompat.from(context).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
        return isOpened;
    }

    //????????????????????????
    private String getId() {
        StringBuilder deviceId = new StringBuilder();
        // ????????????
//        try {
//            //IMEI???imei???
//            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
//            if (!TextUtils.isEmpty(imei)) {
//                deviceId.append("imei");
//                deviceId.append(imei);
//                return deviceId.toString();
//            }
//            //????????????sn???
//            @SuppressLint("MissingPermission") String sn = tm.getSimSerialNumber();
//            if (!TextUtils.isEmpty(sn)) {
//                deviceId.append("sn");
//                deviceId.append(sn);
//                return deviceId.toString();
//            }
//            //???????????????????????? ???????????????id????????????
//            String uuid = getUUID();
//            if (!TextUtils.isEmpty(uuid)) {
//                deviceId.append(uuid);
//                return deviceId.toString();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
            deviceId.append(getUUID());
//        }
        return deviceId.toString();
    }

    /**
     * ??????????????????UUID
     */
    private String uuid;

    public String getUUID() {
        SharedPreferences mShare = getSharedPreferences("uuid", MODE_PRIVATE);
        if (mShare != null) {
            uuid = mShare.getString("uuid", "");
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid", uuid).commit();
        }
        return uuid;
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

    /**
     * ?????????????????????????????????
     * @param bmp
     * @return
     */
    public String saveImageToGallery(Bitmap bmp) {
        // ??????????????????
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //??????io?????????????????????????????????
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storePath + "/" + fileName;
    }

    private Bitmap compressImage(Bitmap image, int quality) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, quality, baos);//???????????????????????????100????????????????????????????????????????????????baos???
        int options = 100;
        while (baos.toByteArray().length / 1024 > 15) {  //?????????????????????????????????????????????32kb,??????????????????
            baos.reset();//??????baos?????????baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//????????????options%?????????????????????????????????baos???
            options -= 1;//???????????????1
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//?????????????????????baos?????????ByteArrayInputStream???
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//???ByteArrayInputStream??????????????????
        return bitmap;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * @param flag (0:????????????????????????1???????????????????????????)
     */
    private void wechatShare(int flag, ShareSdkBean shareSdkBean) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareSdkBean.getUrl();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareSdkBean.getTitle();
        msg.description = shareSdkBean.getTxt();

        //????????????????????????????????????????????????
//        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.wechat);
        Bitmap thumb = null;
        try {
//            String icon = "https://njdeveloptest.obs.cn-east-2.myhuaweicloud.com/app/5814c3c0be2345ada27a39a026b90f2b.jpg?AccessKeyId=CLX6MAVTXA1WBLKJFVI7&Expires=1614242065&Signature=2OCNPomd914jAFlmq6tN3sXAGVk%3D";
            thumb = BitmapFactory.decodeStream(new URL(shareSdkBean.getIcon()).openStream());

            String path = saveImageToGallery(thumb);
            //?????????WXImageObject???WXMediaMessage??????
//            WXImageObject imageObject;
//            if (!TextUtils.isEmpty(path)) {
//                imageObject = new WXImageObject();
//                imageObject.setImagePath(path);
//            } else {
//                imageObject = new WXImageObject(thumb);
//            }
//            msg.mediaObject = imageObject;
            //???????????????
            Bitmap scaledBitmap = BitmapUtil.decodeSampledBitmap(path,120, 150);//Bitmap.createScaledBitmap(thumb, 120, 150, true);
            thumb.recycle();
            msg.thumbData = bmpToByteArray(scaledBitmap, true);

//??????????????????????????????120???150????????????
//???????????????????????????????????????
//            Bitmap thumbBmp = compressImage(thumb, 10);
////Bitmap??????
////            bitmap1.recycle();
//            Log.e(TAG, "wechatShare: "+thumbBmp.getByteCount());
//            msg.thumbData = bmpToByteArray(thumbBmp, true);
//      msg.setThumbImage(thumb);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        msg.setThumbImage(bitmap1);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
//        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        Log.e(TAG, "wechatShare: send" );
        wxApi.sendReq(req);
        Log.e(TAG, "wechatShare: end" );
    }


    /**
     * ?????????????????????QQ
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
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

        Log.e(TAG, "qqFriend: "+shareSdkBean.getIcon());
//        if (TextUtils.isEmpty(shareSdkBean.getIcon())) {//???????????????
//            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://119.45.19.115/file/app/logo.png");//???????????????
//        } else {
//            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, FileUtil.getResourcesUri(mContext, R.drawable.logo));//???????????????
//        }
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
                    mTencent.shareToQQ(MainActivity.this, params, qqShareListener);
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
            Log.e(TAG, "onComplete: share suc"+response);
        }

        @Override
        public void onError(UiError e) {
            Log.e(TAG, "onComplete: share fail"+e.errorDetail);
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.setMainActivity(null);
        EventBus.getDefault().unregister(this);
    }
}

