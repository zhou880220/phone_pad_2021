package com.example.honey_create_cloud_pad.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONObject;
import com.example.honey_create_cloud_pad.BuildConfig;
import com.example.honey_create_cloud_pad.Constant;
import com.example.honey_create_cloud_pad.MyApplication;
import com.example.honey_create_cloud_pad.MyHandlerCallBack;
import com.example.honey_create_cloud_pad.R;
import com.example.honey_create_cloud_pad.bean.BrowserBean;
import com.example.honey_create_cloud_pad.bean.HeadPic;
import com.example.honey_create_cloud_pad.bean.NotificationBean;
import com.example.honey_create_cloud_pad.bean.PictureUpload;
import com.example.honey_create_cloud_pad.bean.RabbitMQBean;
import com.example.honey_create_cloud_pad.bean.TokenIsOkBean;
import com.example.honey_create_cloud_pad.bean.VersionInfo;
import com.example.honey_create_cloud_pad.broadcast.NotificationClickReceiver;
import com.example.honey_create_cloud_pad.file.CleanDataUtils;
import com.example.honey_create_cloud_pad.http.UpdateAppHttpUtil;
import com.example.honey_create_cloud_pad.pushmessage.HuaWeiPushHmsMessageService;
import com.example.honey_create_cloud_pad.util.BaseUtil;
import com.example.honey_create_cloud_pad.util.FileUtil;
import com.example.honey_create_cloud_pad.util.MarketTools;
import com.example.honey_create_cloud_pad.util.NetworkUtils;
import com.example.honey_create_cloud_pad.util.SPUtils;
import com.example.honey_create_cloud_pad.util.SystemUtil;
import com.example.honey_create_cloud_pad.util.VersionUtils;
import com.example.honey_create_cloud_pad.view.AnimationView;
import com.example.honey_create_cloud_pad.webclient.MWebChromeClient;
import com.example.honey_create_cloud_pad.webclient.MWebViewClient;
import com.example.honey_create_cloud_pad.webclient.MyWebViewClient;
import com.example.honey_create_cloud_pad.webclient.WebViewSetting;
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
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.listener.ExceptionHandler;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xj.library.utils.ToastUtils;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.honey_create_cloud_pad.ui.ClipImageActivity.REQ_CLIP_AVATAR;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.newwebprogressbar)
    ProgressBar mNewwebprogressbar;
    @BindView(R.id.new_Web_2)
    BridgeWebView mNewWeb;
//    @InjectView(R.id.loading_page)
//    View mLoadingPage;
    @BindView(R.id.web_error)
    View mWebError;
    @BindView(R.id.tv_fresh)
    Button tvFresh;

    private static final String[] PERMISSIONS_APPLICATION = { //??????????????????
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE};

    private static final int VIDEO_PERMISSIONS_CODE = 1;
    private android.webkit.ValueCallback<Uri[]> mUploadCallbackAboveL;
    private android.webkit.ValueCallback<Uri> mUploadCallbackBelow;
    private Uri imageUri;
    private int REQUEST_CODE = 1234;
    //????????????
    private static final int REQUEST_CAPTURE = 100;
    //????????????
    private static final int REQUEST_PICK = 101;

    private static final String TAG = "MainActivity_TAG";

    //?????????????????????????????????
    private File tempFile;
    private long exitTime = 0;
    //??????
    private static final int NOT_NOTICE = 2;//???????????????????????????
    private AlertDialog alertDialog;
    private AlertDialog mDialog;
    private MWebViewClient mWebViewClient;
    private boolean mBackKeyPressed = false;
    private long mTime;

    private String mVersionName = "";
    private String totalCacheSize = "";
    private String clearSize = "";
    private int type;

    private MyHandlerCallBack.OnSendDataListener mOnSendDataListener;
    private String token1;
    private String userid;
    private String newName = "";
    private String accessToken;
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                newName = (String) msg.obj;
                Log.i(TAG, "newName---" + newName);
                OkHttpClient client1 = new OkHttpClient();
//                final FormBody formBody = new FormBody.Builder()
//                        .add("userId", userid)
//                        .add("url", newName)
//                        .build();
                String post = "{" +
                        "userId:'" + userid + '\'' +
                        ", url:'" + newName + '\'' +
                        '}';
                Log.i(TAG, "post----" + post);
                MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(userid + "=" + newName);
                Log.i(TAG, "stringBuffer----" + stringBuffer.toString());
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
                        Gson gson = new Gson();
                        HeadPic headPic = gson.fromJson(string, HeadPic.class);
                        if (headPic.getCode() == 200) {
                            final String msg1 = headPic.getMsg();
                            final String tete = "mytest";
                            Log.i(TAG, "msg1---" + headPic.getData());
//                            mNewWeb.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mNewWeb.evaluateJavascript("window.sdk.doublePad2(\"" + tete + "\")", new ValueCallback<String>() {
//                                        @Override
//                                        public void onReceiveValue(String value) {
//                                            Log.i(TAG, "msg1-back2--");
//                                        }
//                                    });
//                                }
//                            });

                            mNewWeb.post(new Runnable() {
                                @Override
                                public void run() {
                                    mNewWeb.callHandler("doublePad2", null, new CallBackFunction() {
                                        @Override
                                        public void onCallBack(String data) {
                                            Log.i(TAG, "msg1-back--doublePad2");
                                        }
                                    });

                                }
                            });
                        }else {

                        }
                    }
                });
            }
            return false;
        }
    });

    //??????????????????
    public static List<String> logList = new CopyOnWriteArrayList<String>();

    private String usertoken1;
    private String userid1;
    private MWebChromeClient myChromeWebClient;
    private String backUrl;
    private int back;
    private String myOrder;
    private Channel channel;
    private String receiveMsg;
    private int badgeCount = 1;  //????????????
    private int NOTIFICATION_NUMBER = 0;  //??????????????????
    private String channel_id = "myChannelId";
    private String channel_name = "???????????????";
    private String description = "???????????????";
    NotificationManager notificationManager;
    private boolean ChaceSize = true;
    private RabbitMQBean rabbitMQBean;
    private HashMap<String, String> hashMap = new HashMap<String, String>();
    private String zxIdTouTiao;
    private HuaWeiPushHmsMessageService huaWeiPushHmsMessageOnClick;
    private String imei = "";
    private String huaWeiToken;
    private String oppoToken;
    private Unbinder unbinder;
//    private String content_url;
    private int NOTIFICATION_SHOW_SHOW_AT_MOST = 5;
    private boolean isPrepareFinish = false;
    private static final int WAIT_INTERVAL = 2000;
    private Context mContext;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
//        EventBus.getDefault().register(this);
        myRequetPermission(PERMISSIONS_APPLICATION);

        boolean hasNet = checkNet();

        initClick();
        if (!hasNet) {
            createNotificationChannel();
            initPush(); //??????????????????

//            Date d = new Date();
//            (String)SPUtils.getInstance().get("context_url", "");
//            Log.i(TAG, "onCreate: 2"+content_url);
            webView(Constant.text_url);
            iniVersionName();

            Uri uri = getIntent().getData();
            if (uri != null) {
                String id = uri.getQueryParameter("thirdId");
                String open = uri.getQueryParameter("open");
                Log.e(TAG, "huaweiUrl open: "+open);
                if (id != null) {
                    Intent intent = new Intent(this, NewsActivity.class);
                    intent.putExtra("url", id);
                    startActivity(intent);
                }

                if (open != null) {
                    Log.e(TAG, "huaweiUrl: " + uri);
                    //test://zzy:8080/home?open=message&appid=2&appName=????????????????????????  ????????????????????????
                    String huaWei = uri.getQueryParameter("appid");
                    String appName = uri.getQueryParameter("appName");
                    JSONObject jsonObject  = new JSONObject();
                    jsonObject.put("appid",huaWei);
                    jsonObject.put("appName",appName);
                    String s = jsonObject.toJSONString();
                    Log.e(TAG, "onNewIntent: "+s);
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
            Log.i(TAG, "onCreate: 1");


            Intent intent1 = getIntent();
            String app_notice_list = intent1.getStringExtra("APP_NOTICE_LIST");
            Log.e(TAG, "onCreate: " + app_notice_list);
            if (app_notice_list != null) {
                if (app_notice_list.equals("??????")) { // ?????????????????????
                    webView(Constant.MyNews);
                } else if (app_notice_list.equals("??????")) { //?????????????????????
                    String xiaomiMessage = intent1.getStringExtra("pushContentMessage");
//                SharedPreferences xiaomiPref = getSharedPreferences("xiaomiPushMessage",MODE_PRIVATE);
//                SharedPreferences.Editor edit = xiaomiPref.edit();
//                edit.putString("??????????????????",xiaomiMessage);
//                edit.commit();
                    Log.e(TAG, "xiaomipush:2 "+xiaomiMessage );
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
        }

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateApp();
            }
        }, 5000);
    }

    private void initClick(){
        tvFresh.setOnClickListener(v->{
            if (NetworkUtils.isConnected()) {
                this.recreate();
            }else {
                showAlertDialog("????????????","?????????????????????????????????");
            }

        });
    }

    public void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkNet();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkNet() {
        Log.i("pad_start", "checkNet: "+NetworkUtils.isConnected());
        boolean flag = false;
        if (!NetworkUtils.isConnected()) {
            mWebError.setVisibility(View.VISIBLE);
            flag = true;
        }else {
            if (mWebError.getVisibility() == View.VISIBLE) {
                mWebError.setVisibility(View.GONE);
            }
            flag =false;
        }
        return flag;
    }



    //????????????
    private void initScreen() {
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    /**
     * ??????????????????
     */
    private void initPush() {
//        if (android.os.Build.BRAND.toLowerCase().contains("xiaomi")) {
//            //???????????????????????????
//            MyApplication.setMainActivity(this);
//            //????????????????????????
//            MiPushClient.subscribe(this, "ALL", null);
//        }
//        if (android.os.Build.BRAND.toLowerCase().contains("vivo")) {
//            //??????Vivo????????????
//            PushClient.getInstance(this).setTopic("ALL", new IPushActionListener() {
//                @Override
//                public void onStateChanged(int state) {
//                    if (state != 0) {
//                        Log.e(TAG, "????????????????????????" + state);
//                    } else {
//                        Log.e(TAG, "????????????????????????");
//                    }
//                }
//            });
//        }
        //??????huawei????????????
        if (android.os.Build.BRAND.toLowerCase().contains("huawei")) {
            SharedPreferences huaWeiPushPref = getSharedPreferences("HuaWeiPushToken", MODE_PRIVATE);
            //???????????????????????????????????????????????????
            huaWeiToken = huaWeiPushPref.getString("huaWeiToken", "");
            if (!huaWeiToken.isEmpty()) {
                Log.e(TAG, "HuaweiPushRequest: 1" + huaWeiToken);
                PushTokenRelation("1", huaWeiToken, "2");
            } else {
                getRabbitMQAddressOkhttp();//??????RabbitMq??????????????????
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
                                getRabbitMQAddressOkhttp();//??????RabbitMq??????????????????
                                Log.e(TAG, "subscribe failed: ret=" + task.getException().getMessage());
                            }
                        }
                    });
        }else {//????????????mq??????
            getRabbitMQAddressOkhttp();//??????RabbitMq??????????????????
        }

//        if (android.os.Build.BRAND.toLowerCase().contains("oppo")) {
//            SharedPreferences oppoPushPref = getSharedPreferences("OppoPushToken", MODE_PRIVATE);
//            oppoToken = oppoPushPref.getString("OppoToken", "");
//            if (!oppoToken.isEmpty()) {
//                Log.e(TAG, "oppoPushRequest: 1" + oppoToken);
//                PushTokenRelation("1", oppoToken, "4");
//            } else {
//                Log.e(TAG, "oppoPushRequest: 2" + "??????token");
//            }
//        }
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

    /**
     * ??????????????????token
     */
    private void deleteToken() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    HmsInstanceId.getInstance(MainActivity.this).deleteToken(appId, "HCM");
                    //????????????
                    SharedPreferences sb = MainActivity.this.getSharedPreferences("HuaWeiPushToken", MODE_PRIVATE);
                    SharedPreferences.Editor edit = sb.edit();
                    edit.clear();
                    edit.commit();
                    Log.i(TAG, "deleteToken success.");
                } catch (ApiException e) {
                    Log.e(TAG, "deleteToken failed." + e);
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


    /**
     * ?????????webview js??????
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void webView(String url) {
        Log.i(TAG, "webView url: "+ url +" mNewWeb: "+mNewWeb);
        if (Build.VERSION.SDK_INT >= 19) {
            mNewWeb.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mNewWeb.getSettings().setLoadsImagesAutomatically(false);
        }
        WebSettings webSettings = mNewWeb.getSettings();
        String userAgentString = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgentString + "; application-center-pad");

        if (webSettings != null) {
            WebViewSetting.initweb(webSettings);
        }
        //Handler????????????????????????????????????????????????H5???????????????Native?????????????????????h5??????send()??????????????????????????????MyHandlerCallBack
        mNewWeb.setDefaultHandler(new MyHandlerCallBack(mOnSendDataListener));
        myChromeWebClient = new MWebChromeClient(this, mNewwebprogressbar, mWebError);
        mNewWeb.setWebChromeClient(myChromeWebClient);
        MyWebViewClient myWebViewClient = new MyWebViewClient(mNewWeb, mWebError);
        myWebViewClient.setOnCityClickListener(new MyWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {  //??????????????????????????????
                myOrder = name;
                if (name != null) {
//                    if (name.equals(Constant.login_url)) {
//                    mTextPolicyReminder.setVisibility(View.VISIBLE);
//                    mCloseLoginPage.setVisibility(View.VISIBLE);
//                    mTextPolicyReminderBack.setVisibility(View.VISIBLE);
//                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//                } else if (name.equals(Constant.register_url)) {
//                    mTextPolicyReminder.setVisibility(View.VISIBLE);
//                    mCloseLoginPage.setVisibility(View.VISIBLE);
//                    mTextPolicyReminderBack.setVisibility(View.GONE);
//                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//                } else if (name.contains("/about")) {
//                    mTextPolicyReminder.setVisibility(View.VISIBLE);
//                    mCloseLoginPage.setVisibility(View.GONE);
//                    mTextPolicyReminderBack.setVisibility(View.VISIBLE);
//                } else {
//                    pageReload = true;
//                    mTextPolicyReminder.setVisibility(View.GONE);
//                    mCloseLoginPage.setVisibility(View.GONE);
//                    mTextPolicyReminderBack.setVisibility(View.GONE);
//                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);  //SOFT_INPUT_ADJUST_RESIZE
//                }
                }
            }
        });
        mNewWeb.setWebViewClient(myWebViewClient);
        mNewWeb.loadUrl(url);
//        mNewWeb.loadUrl("file:///android_asset/lll.html");

        //js??????????????????
//        mNewWeb.addJavascriptInterface(new MJavaScriptInterface(getApplicationContext()), "ApplyFunc");

        //????????????
        mNewWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e(TAG, "onKey: web back"+keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.e(TAG, "onKey: web back  1");
                    if (mNewWeb != null && mNewWeb.canGoBack()) {
                        Log.e(TAG, "onKey: web back  2"+myOrder);
                        SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                        String userInfo = sb.getString("userInfo", "");
                        if (myOrder.contains("/home")) { //???????????????????????????  ??????????????????
                            if ((System.currentTimeMillis() - exitTime) > 2000) {
                                ToastUtils.show("????????????????????????");
                                exitTime = System.currentTimeMillis();
                                Log.e(TAG, "show tip: " +exitTime);
                            } else {
                                Log.e(TAG, "exit: app" );
                                finish();
                            }
                            mNewWeb.goBack();
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

        //??????????????????????????????Handler?????????  ???????????????
        mNewWeb.registerHandler("getVersionName", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (!mVersionName.isEmpty()) {
//                    mEditText.setText("????????????Native?????????????????????\n" + data);
                    int sysVersion = VersionUtils.getVersion(mContext);
                    VersionInfo versionInfo = new VersionInfo(mVersionName, sysVersion);
                    function.onCallBack(new Gson().toJson(versionInfo));
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

        //????????????
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

        //?????????????????????
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

        //??????
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

        //??????
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

        /**
         * ????????????????????????
         */
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
                        SPUtils.getInstance().put("token", usertoken);
                        Log.e(TAG, "??????????????????: " + usertoken + "---" + userID);
                        if (!usertoken.isEmpty()) {
                            usertoken1 = usertoken;
                            userid1 = userID;
                            if (android.os.Build.BRAND.toLowerCase().contains("huawei")) {
                                PushTokenRelation("2", huaWeiToken, "2");
                                getHuaweiToken();
                            }
//                            if (android.os.Build.BRAND.toLowerCase().contains("xiaomi")) {
//                                //????????????????????????
//                                MiPushClient.setAlias(MainActivity.this, userid1, null);
//                            }
//                            if (android.os.Build.BRAND.toLowerCase().contains("vivo")) {
//                                //??????Vivo????????????
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
//                            }
//                            if (android.os.Build.BRAND.toLowerCase().contains("oppo")) {
//                                PushTokenRelation("2", oppoToken, "4");
//                            }

                            SharedPreferences sb = MainActivity.this.getSharedPreferences("NotificationUserId", MODE_PRIVATE);
                            SharedPreferences.Editor edit = sb.edit();
                            edit.putString("NotifyUserId", usertoken1);
                            edit.commit();



                            //??????userId????????????
                            String notifyUserId = sb.getString("NotifyUserId", "");
//                deleteUserQueue(); //????????????
                            //??????????????????????????????7.0  ?????????????????????????????? ?????????????????????
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                if (!TextUtils.isEmpty(notifyUserId)) {
                                    notificationChange(userid1, "0");
                                    new Thread(() ->{ basicConsume(myHandler); }).start();
                                }
                            } else {
                                Log.e(TAG, "????????????????????????7.0");
                            }
                        }
                    }else{}
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
                        Log.e(TAG, "???????????????: " + data);
                        Map map = JSONObject.parseObject(data, Map.class);
                        String redirectUrl = (String) map.get("redirectUrl");
                        int appLyId = (int) map.get("appId");
                        String appId = String.valueOf(appLyId);
                        if (!redirectUrl.isEmpty()) {
                            Log.e(TAG, "???????????????:2 " + redirectUrl);
                            if (zxIdTouTiao == null || zxIdTouTiao.isEmpty()) {
                                Log.e(TAG, "???????????????:3 " + redirectUrl);
                                Intent intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
                                intent.putExtra("url", redirectUrl);
                                intent.putExtra("token", usertoken1);
                                intent.putExtra("userid", userid1);
                                intent.putExtra("appId", appId);
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "???????????????:4" + redirectUrl);
                                Intent intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
                                intent.putExtra("url", redirectUrl);
                                intent.putExtra("token", usertoken1);
                                intent.putExtra("userid", userid1);
                                intent.putExtra("appId", appId);
                                intent.putExtra("zxIdTouTiao", zxIdTouTiao);
                                startActivity(intent);
                                zxIdTouTiao = "";
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
        }); //????????????
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
                try {
                    if (!data.isEmpty()) {
                        Log.e(TAG, "??????????????????: " + data);
                        Map map = JSONObject.parseObject(data, Map.class);
                        String link = (String) map.get("link");
                        String code = (String) map.get("code");
                        String token = (String) map.get("token");
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
//        mNewWeb.registerHandler("CashierDeskGo", new BridgeHandler() {
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                try {
//                    if (!data.isEmpty()) {
//                        Log.e(TAG, "??????????????????: " + data);
//                        Map map = JSONObject.parseObject(data, Map.class);
//                        String userId = (String) map.get("userId");
//                        String orderNo = (String) map.get("orderNo");
//                        String outTradeNo = (String) map.get("outTradeNo");
//
//                        pageReload = true;
//                        Intent intent = new Intent(MainActivity.this, IntentOpenActivity.class);
//                        intent.putExtra("userId", userId);
//                        intent.putExtra("orderNo", orderNo);
//                        intent.putExtra("outTradeNo", outTradeNo);
//                        intent.putExtra("token", usertoken1);
//                        startActivity(intent);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

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
                    Log.i(TAG, "handler: logout");
                    ChaceSize = true;
                    //??????????????????
//                    iniVersionName();
//                    if (android.os.Build.BRAND.toLowerCase().contains("xiaomi")) {
//                        //??????????????????????????????????????????
//                        MiPushClient.unsetAlias(MainActivity.this, userid1, null);
//                    }
//                    if (android.os.Build.BRAND.toLowerCase().contains("vivo")) {
//                        //????????????????????????Vivo????????????
//                        PushClient.getInstance(MainActivity.this).unBindAlias(userid1, new IPushActionListener() {
//                            @Override
//                            public void onStateChanged(int state) {
//                                if (state != 0) {
//                                    Log.e(TAG, "??????????????????" + state);
//                                } else {
//                                    Log.e(TAG, "??????????????????");
//                                }
//                            }
//                        });
//                    }
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

//                        deleteToken();
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

//                    BaseUtil.restartActivity(MainActivity.this);
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

//        imei = SystemUtil.getIMEI(MainActivity.this);

        if (PushFuncType.equals("1")) {
            String PushHuaweiBody = "{" +
                    "accessEquipment:'" + accessEquipmentType + '\'' +
                    ", equipmentType:'" + "2" + '\'' +
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
                    Log.e(TAG, "HuaweiPushRequest:3 " + string);
                }
            });
        } else if (PushFuncType.equals("2")) {
            Log.e(TAG, "PushTokenRelation:userid1: " + userid1);
            String PushHuaweiBody = "{" +
                    "accessEquipment:'" + accessEquipmentType + '\'' +
                    ", equipmentType:'" + "2" + '\'' +
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
            Log.e(TAG, "PushTokenRelation:userid1: " + userid1);
            String PushHuaweiBody = "{" +
                    "accessEquipment:'" + accessEquipmentType + '\'' +
                    ", equipmentType:'" + "2" + '\'' +
                    ", equipmentIdCode:'" + imei + '\'' +
                    ", status:'" + "0" + '\'' +
                    ", token:'" + UserPushToken + '\'' +
                    ", userId:'" + userid1 + '\'' +
                    '}';

            MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");
            RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, PushHuaweiBody);
            Log.e(TAG, "update PushTokenRelation: "+PushHuaweiBody);
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
                    Log.e(TAG, "HuaweiPushRequest:4 " + string);
                }
            });
        } else if (PushFuncType.equals("4")) {
            String PushHuaweiBody = "{" +
                    "accessEquipment:'" + accessEquipmentType + '\'' +
                    ", equipmentType:'" + "2" + '\'' +
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
                Log.e(TAG, "notificationChange onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.i(TAG, "notificationChange onResponse: "+string);
            }
        });
    }

    /**
     * ??????????????????
     */
    private void gotoCamera() {
        Log.d(TAG, "*****************????????????********************");
        //?????????????????????????????????
        tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/myImage/"), System.currentTimeMillis() + ".jpg");

        //???????????????????????????
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //??????7.0???????????????????????????????????????xml/file_paths.xml
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * ???????????????
     */
    private void gotoPhoto() {
        Log.d(TAG, "*****************????????????********************");
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
        badgeCount = 1;
        notificationManager.cancel(NOTIFICATION_NUMBER);
        ShortcutBadger.removeCount(this);
//        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//
//            }
//        });

        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
//        initScreen();
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
//        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//
//            }
//        });
        badgeCount = 1;
        notificationManager.cancel(NOTIFICATION_NUMBER);
        ShortcutBadger.removeCount(this);
        SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
        String apply_url = sp1.getString("apply_url", "");//???????????????????????????????????????????????????
        Log.e(TAG, "onStart: "+apply_url);
        if (!TextUtils.isEmpty(apply_url)) {
            webView(apply_url);
        }
        SharedPreferences.Editor edit = sp1.edit();
        edit.clear();
        edit.commit();

//        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//            }
//        });
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestart() {
//        SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
//        String apply_url = sp1.getString("apply_url", "");//???????????????????????????????????????????????????
//        if (!TextUtils.isEmpty(apply_url)) {
//            webView(apply_url);
//        }
//        SharedPreferences.Editor edit = sp1.edit();
//        edit.clear();
//        edit.commit();
//        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//
//            }
//        });

        removeNotice();

//        initScreen();
        super.onRestart();
    }

    private void removeNotice () {
        badgeCount = 1;
        ShortcutBadger.removeCount(this);
        notificationManager.cancel(NOTIFICATION_NUMBER);
        boolean notificationEnabled = isNotificationEnabled(this);
        if (notificationEnabled == true && usertoken1 != null) {
            notificationChange(userid1, "0");
        } else {

        }
//        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//
//            }
//        });
        SharedPreferences sb = getSharedPreferences("NotificationUserId", MODE_PRIVATE);
        String notifyUserId = sb.getString("NotifyUserId", "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!TextUtils.isEmpty(notifyUserId)) {
                new Thread(() -> basicConsume(myHandler)).start();
            }
        } else {
            Log.e(TAG, "????????????????????????7.0");
        }
    }

    @Override
    protected void onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new Thread(() -> basicConsume(myHandler)).start();
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
    private void iniVersionName() {
        totalCacheSize = CleanDataUtils.getTotalCacheSize(Objects.requireNonNull(MainActivity.this));
        try {
            //??????????????????
            PackageManager packageManager = getPackageManager();
            //?????????????????????
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //???????????????
            mVersionName = packageInfo.versionName;
            Log.i(TAG, mVersionName+"   "+ totalCacheSize);
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_APPLICATION, 1);
        } else {
//            mLodingTime();
        }
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
//        if (requestCode == 1) {
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] == PERMISSION_GRANTED) {//???????????????????????????
////                    mLodingTime();
//                } else {
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//?????????????????????????????????
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        builder.setTitle("permission")
//                                .setMessage("????????????????????????????????????app???")
//                                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        if (mDialog != null && mDialog.isShowing()) {
//                                            mDialog.dismiss();
//                                        }
//                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                        Uri uri = Uri.fromParts("package", getPackageName(), null);//????????????"package",???????????????????????????
//                                        intent.setData(uri);
//                                        startActivityForResult(intent, NOT_NOTICE);
//                                    }
//                                });
//                        mDialog = builder.create();
//                        mDialog.setCanceledOnTouchOutside(false);
//                        mDialog.show();
////                        mLodingTime();
//                    } else {//????????????
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        builder.setTitle("permission")
//                                .setMessage("????????????????????????????????????app???")
//                                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        if (alertDialog != null && alertDialog.isShowing()) {
//                                            alertDialog.dismiss();
//                                        }
//                                        ActivityCompat.requestPermissions(MainActivity.this,
//                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
//                                    }
//                                });
//                        alertDialog = builder.create();
//                        alertDialog.setCanceledOnTouchOutside(false);
//                        alertDialog.show();
//                    }
//                }
//            }
//        }
    }

    //???????????????
//    private void showDialog() {
//        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
//                .setTitle("??????")
//                .setMessage("??????????????????????????????????????????????????????????????????")
//                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        goToAppSetting();
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }

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
                    String realPathFromUri = getRealPathFromUri(this, uri);
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
                    Log.i(TAG, "onActivityResult: fileSize =" + fileSize * 1.0f / 1024);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String date = simpleDateFormat.format(new Date());
                    FileUtil.saveBitmapToSDCard(bitMap, "123");
                    //?????????????????????bitMap?????????????????????????????????
                    //......
                    Log.i(TAG, "Bearer" + " " + token1);
                    accessToken = "Bearer" + " " + token1;
                    Log.i(TAG, "accessToken---" + accessToken);
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
                            Gson gson = new Gson();
                            Log.i(TAG, "response---" + s);
                            PictureUpload pictureUpload = gson.fromJson(s, PictureUpload.class);
                            if (pictureUpload.getCode() == 200) {
                                List<PictureUpload.DataBean> data = pictureUpload.getData();
                                Message message = myHandler.obtainMessage();
                                message.obj = data.get(0).getNewName();
                                message.what = 1;
                                myHandler.sendMessage(message);
                                Log.i(TAG, "pictureUpload.getMsg()---" + pictureUpload.getMsg());
                                Log.i(TAG, "newName---" + MainActivity.this.newName);
                            } else {
                            }
                        }
                    });
                }
                break;
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
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
         * ????????????url??????
         *
         * @param interfaceUrl
         * @param appId
         * @param token
         */
        @JavascriptInterface
        public void showApplyParams(String interfaceUrl, String appId, String token) {
            Log.i("??????js???Toast", interfaceUrl);
            if (!interfaceUrl.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
                intent.putExtra("url", interfaceUrl);
                intent.putExtra("token", usertoken1);
                intent.putExtra("userid", userid1);
                intent.putExtra("appId", appId);
                Log.i(TAG, "showApplyParamstoken1---" + usertoken1 + "____" + userid1);
                startActivity(intent);
            } else {
                Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
            }

        }

        @JavascriptInterface
        public void showNewsParams(String addressUrl, String appId, String token) {
            if (!addressUrl.isEmpty()) {
                Log.i("??????js???Toast", addressUrl);
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
            Log.i("radioCode--", "1321");
            gotoSet();
        }

        /**
         * ??????????????????token
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
                    if (!TextUtils.isEmpty(notifyUserId)) {
                        notificationChange(userid1, "0");
                        new Thread(() -> basicConsume(myHandler)).start();
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

    /**
     * ?????????????????????????????????????????????
     */
    private void basicConsume(final Handler handler) {
        try {
            //??????
            Connection connection = getConnection();
            if (connection != null) {
                //??????
                channel = connection.createChannel();
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
                        Log.i(TAG, "basicConsume: "+receiveMsg);
                        NotificationConsune();
                    }
                };
                Log.i(TAG, "basicConsume: "+ userid);
                channel.basicConsume("app.notice.queue."+userid1, true, "administrator", consumer);
            }
            Log.e(TAG, "basicConsume: end");
        } catch (IOException e) {
            Log.e(TAG, "basicConsume: "+ e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     */
    private Connection getConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        if (rabbitMQBean != null) {
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
                Log.e(TAG, "NotificationConsune onFailure: "+e.getMessage());

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Gson gson = new Gson();
                TokenIsOkBean tokenIsOkBean = gson.fromJson(string, TokenIsOkBean.class);
                Log.e(TAG, "NotificationConsune onResponse: "+string);

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
        NOTIFICATION_NUMBER++;
        if(NOTIFICATION_NUMBER > NOTIFICATION_SHOW_SHOW_AT_MOST) {
            NOTIFICATION_NUMBER = 1;
        }
        Gson gson1 = new Gson();
        Intent intent = new Intent(this, NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                    .setColor(getResources().getColor(R.color.notice_tip))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setNumber(badgeCount)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
            notificationManager.notify(NOTIFICATION_NUMBER, notification);
            mNewWeb.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
//                    mNewWeb.evaluateJavascript("window.sdk.noticeTimes(\"" + badgeCount + "\")", new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String value) {
//                            Log.e(TAG, "sendNotification: back");
//                        }
//                    });

                    mNewWeb.callHandler("noticeTimes", (badgeCount-1) + "", new CallBackFunction() {
                        @Override
                        public void onCallBack(String data) {
                            Log.e(TAG, "sendNotification: back2");
                        }
                    });
                }
            });
        } else {
            //????????????
        }
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

//    @Override
//    public void onBackPressed() {
//        Log.e(TAG, "onBackPressed: "+isPrepareFinish);
//        if(!isPrepareFinish){
//            new Handler().postDelayed(
//                    () -> isPrepareFinish = false, WAIT_INTERVAL
//            );
//            isPrepareFinish = true;
//            Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            super.onBackPressed();
//        }
//    }

//    /**
//     *  ??????????????????
//     * @param keyCode
//     * @param event
//     * @return
//     */
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.e(TAG, "onKeyDown: "+ (keyCode == KeyEvent.KEYCODE_BACK) );
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Log.e(TAG, "what?: " +(System.currentTimeMillis() - exitTime));
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                ToastUtils.show("????????????????????????");
//                exitTime = System.currentTimeMillis();
//                Log.e(TAG, "show tip: " +exitTime);
//            } else {
//                Log.e(TAG, "exit: app" );
////                super.finish();
//            }
//            Log.e(TAG, "what2: " +exitTime);
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = getIntent().getData(); //????????????????????????  ?????????????????????  oppo????????????
        Log.e(TAG, "resume huaweiUrl: "+uri);
        if (uri != null) {
            String thirdId = uri.getQueryParameter("thirdId");
            if (thirdId != null) {
                intent = new Intent(this, NewsActivity.class);
                intent.putExtra("url", thirdId);
                startActivity(intent);
            }
            String open = uri.getQueryParameter("open");
            Log.e(TAG, "resume huaweiUrl open: "+open);
            if (open.equals("message")) {
                Log.e(TAG, "huaweiUrl: "+uri);
                //test://zzy:8080/home?open=message&appid=2&appName=????????????????????????  ????????????????????????
                String huaWei = uri.getQueryParameter("appid");
                String appName = uri.getQueryParameter("appName");
                JSONObject jsonObject  = new JSONObject();
                jsonObject.put("appid",huaWei);
                jsonObject.put("appName",appName);
                String s = jsonObject.toJSONString();
                Log.e(TAG, "onNewIntent: "+s);
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
                Log.e(TAG, "xiaomiMessage: "+xiaomiMessage );
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
                }, 3000);//2????????????Runnable??????run??????
            }
        }
    }

    //????????????????????????
    private String getId() {
        StringBuilder deviceId = new StringBuilder();
        // ????????????
        try {
            //IMEI???imei???
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //????????????sn???
            @SuppressLint("MissingPermission") String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //???????????????????????? ???????????????id????????????
            String uuid = getUUID();
            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append(getUUID());
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.setMainActivity(null);
        unbinder.unbind();
//        EventBus.getDefault().unregister(this);
    }
}
