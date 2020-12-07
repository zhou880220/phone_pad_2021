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
import com.example.honey_create_cloud_pad.broadcast.NotificationClickReceiver;
import com.example.honey_create_cloud_pad.file.CleanDataUtils;
import com.example.honey_create_cloud_pad.pushmessage.HuaWeiPushHmsMessageService;
import com.example.honey_create_cloud_pad.util.BaseUtil;
import com.example.honey_create_cloud_pad.util.FileUtil;
import com.example.honey_create_cloud_pad.util.MarketTools;
import com.example.honey_create_cloud_pad.util.NetworkUtils;
import com.example.honey_create_cloud_pad.util.SPUtils;
import com.example.honey_create_cloud_pad.util.SystemUtil;
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
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.mipush.sdk.MiPushClient;

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

    private static final String[] PERMISSIONS_APPLICATION = { //应用中心授权
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE};

    private static final int VIDEO_PERMISSIONS_CODE = 1;
    private android.webkit.ValueCallback<Uri[]> mUploadCallbackAboveL;
    private android.webkit.ValueCallback<Uri> mUploadCallbackBelow;
    private Uri imageUri;
    private int REQUEST_CODE = 1234;
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;

    private static final String TAG = "MainActivity_TAG";

    //调用照相机返回图片文件
    private File tempFile;
    private long exitTime;
    //权限
    private static final int NOT_NOTICE = 2;//如果勾选了不再询问
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

    //小米推送信息
    public static List<String> logList = new CopyOnWriteArrayList<String>();

    private String usertoken1;
    private String userid1;
    private MWebChromeClient myChromeWebClient;
    private String backUrl;
    private int back;
    private String myOrder;
    private Channel channel;
    private String receiveMsg;
    private int badgeCount = 1;  //角标叠加
    private int NOTIFICATION_NUMBER = 0;  //通知条数堆叠
    private String channel_id = "myChannelId";
    private String channel_name = "蜂巢制造云";
    private String description = "通知的功能";
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
    private String content_url;
    private int NOTIFICATION_SHOW_SHOW_AT_MOST = 5;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        myRequetPermission(PERMISSIONS_APPLICATION);

        boolean hasNet = checkNet();

        initClick();
        if (!hasNet) {
            createNotificationChannel();
            initPush(); //平台群推注册

            content_url = (String)SPUtils.getInstance().get("context_url", "");
            Log.i(TAG, "onCreate: 2"+content_url);
            webView(content_url);//Constant.text_url);
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
                    //test://zzy:8080/home?open=message&appid=2&appName=精益生产电子看板  用户华为通知跳转
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
                            //要执行的操作
                            mNewWeb.callHandler("PushMessageIntent", s, new CallBackFunction() {
                                @Override
                                public void onCallBack(String data) {

                                }
                            });
                        }
                    }, 1000);//2秒后执行Runnable中的run方法
                }
            }
            Log.i(TAG, "onCreate: 1");


            Intent intent1 = getIntent();
            String app_notice_list = intent1.getStringExtra("APP_NOTICE_LIST");
            Log.e(TAG, "onCreate: " + app_notice_list);
            if (app_notice_list != null) {
                if (app_notice_list.equals("咨询")) { // 跳转到咨询页面
                    webView(Constant.MyNews);
                } else if (app_notice_list.equals("消息")) { //跳转到消息页面
                    String xiaomiMessage = intent1.getStringExtra("pushContentMessage");
//                SharedPreferences xiaomiPref = getSharedPreferences("xiaomiPushMessage",MODE_PRIVATE);
//                SharedPreferences.Editor edit = xiaomiPref.edit();
//                edit.putString("小米推送消息",xiaomiMessage);
//                edit.commit();
                    Log.e(TAG, "xiaomipush:2 "+xiaomiMessage );
                    webView(Constant.APP_NOTICE_LIST);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //要执行的操作
                            mNewWeb.callHandler("PushMessageIntent", xiaomiMessage, new CallBackFunction() {
                                @Override
                                public void onCallBack(String data) {

                                }
                            });
                        }
                    }, 1000);//2秒后执行Runnable中的run方法
                }
            }
        }
    }

    private void initClick(){
        tvFresh.setOnClickListener(v->{
            if (NetworkUtils.isConnected()) {
                this.recreate();
            }else {
                showAlertDialog("温馨提示","请确保您的设备已联网！");
            }

        });
    }

    public void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
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



    //全屏显示
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
     * 平台群推注册
     */
    private void initPush() {
//        if (android.os.Build.BRAND.toLowerCase().contains("xiaomi")) {
//            //初始化小米推送服务
//            MyApplication.setMainActivity(this);
//            //注册小米群推服务
//            MiPushClient.subscribe(this, "ALL", null);
//        }
//        if (android.os.Build.BRAND.toLowerCase().contains("vivo")) {
//            //注册Vivo群推服务
//            PushClient.getInstance(this).setTopic("ALL", new IPushActionListener() {
//                @Override
//                public void onStateChanged(int state) {
//                    if (state != 0) {
//                        Log.e(TAG, "设置群推标签异常" + state);
//                    } else {
//                        Log.e(TAG, "设置群推标签成功");
//                    }
//                }
//            });
//        }
        //注册huawei群推服务
        if (android.os.Build.BRAND.toLowerCase().contains("huawei")) {
            SharedPreferences huaWeiPushPref = getSharedPreferences("HuaWeiPushToken", MODE_PRIVATE);
            //从其它页面回调，并加载要回调的页面
            huaWeiToken = huaWeiPushPref.getString("huaWeiToken", "");
            if (!huaWeiToken.isEmpty()) {
                Log.e(TAG, "HuaweiPushRequest: 1" + huaWeiToken);
                PushTokenRelation("1", huaWeiToken, "2");
            } else {
                getRabbitMQAddressOkhttp();//获取RabbitMq推送服务地址
                Log.e(TAG, "HuaweiPushRequest: 2" + "没有token");
            }

            HmsMessaging.getInstance(MainActivity.this)
                    .subscribe("ALL")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.e(TAG, "subscribe Complete");
                            } else {
                                getRabbitMQAddressOkhttp();//获取RabbitMq推送服务地址
                                Log.e(TAG, "subscribe failed: ret=" + task.getException().getMessage());
                            }
                        }
                    });
        }else {//非华为走mq推送
            getRabbitMQAddressOkhttp();//获取RabbitMq推送服务地址
        }

//        if (android.os.Build.BRAND.toLowerCase().contains("oppo")) {
//            SharedPreferences oppoPushPref = getSharedPreferences("OppoPushToken", MODE_PRIVATE);
//            oppoToken = oppoPushPref.getString("OppoToken", "");
//            if (!oppoToken.isEmpty()) {
//                Log.e(TAG, "oppoPushRequest: 1" + oppoToken);
//                PushTokenRelation("1", oppoToken, "4");
//            } else {
//                Log.e(TAG, "oppoPushRequest: 2" + "没有token");
//            }
//        }
    }

    /**
     * 用户登录获取华为手机Token
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
     * 删除华为手机token
     */
    private void deleteToken() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    HmsInstanceId.getInstance(MainActivity.this).deleteToken(appId, "HCM");
                    //清除缓存
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

    /**
     * 获取RabbitMQ服务地址
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
     * 初始化webview js交互
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
        //Handler做为通信桥梁的作用，接收处理来自H5数据及回传Native数据的处理，当h5调用send()发送消息的时候，调用MyHandlerCallBack
        mNewWeb.setDefaultHandler(new MyHandlerCallBack(mOnSendDataListener));
        myChromeWebClient = new MWebChromeClient(this, mNewwebprogressbar, mWebError);
        mNewWeb.setWebChromeClient(myChromeWebClient);
        MyWebViewClient myWebViewClient = new MyWebViewClient(mNewWeb, mWebError);
        myWebViewClient.setOnCityClickListener(new MyWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {  //动态监听页面加载链接
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

        //js交互接口定义
//        mNewWeb.addJavascriptInterface(new MJavaScriptInterface(getApplicationContext()), "ApplyFunc");

        //回退监听
        mNewWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mNewWeb != null && mNewWeb.canGoBack()) {
                        SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                        String userInfo = sb.getString("userInfo", "");
                        if (myOrder.contains("/home")) { //首页拦截物理返回键  直接关闭应用
                            finish();
                        } else if (myOrder.contains("/information")) { //确保从该页面返回的是首页
                            webView(content_url);//Constant.text_url);
                        } else {
                            mNewWeb.goBack();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        //有方法名的都需要注册Handler后使用  获取版本号
        mNewWeb.registerHandler("getVersionName", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (!mVersionName.isEmpty()) {
//                    mEditText.setText("通过调用Native方法接收数据：\n" + data);
                    function.onCallBack("版本号V" + mVersionName);
                }
            }
        });

        /**
         * 存储用户信息
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

        //初始缓存
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

        //用户点击后缓存
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

        //拍照
        mNewWeb.registerHandler("getTakeCamera", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    //权限判断
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        //申请READ_EXTERNAL_STORAGE权限
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

        //相册
        mNewWeb.registerHandler("getPhotoAlbum", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    //权限判断
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        //申请READ_EXTERNAL_STORAGE权限
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
         * 用户加载初始通知
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

        //存储用户登录页面传递的信息
        mNewWeb.registerHandler("setUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    Log.e(TAG, "获取用户登录信息: " + data);
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

        //向页面传递用户登录基本信息
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

        //获取用户登录token userID
        mNewWeb.registerHandler("getToken", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Map map = JSONObject.parseObject(data, Map.class);
                        String usertoken = (String) map.get("token");
                        String userID = (String) map.get("userID");
                        SPUtils.getInstance().put("token", usertoken);
                        Log.e(TAG, "用户登录信息: " + usertoken + "---" + userID);
                        if (!usertoken.isEmpty()) {
                            usertoken1 = usertoken;
                            userid1 = userID;
                            if (android.os.Build.BRAND.toLowerCase().contains("huawei")) {
                                PushTokenRelation("2", huaWeiToken, "2");
                                getHuaweiToken();
                            }
//                            if (android.os.Build.BRAND.toLowerCase().contains("xiaomi")) {
//                                //注册小米单推服务
//                                MiPushClient.setAlias(MainActivity.this, userid1, null);
//                            }
//                            if (android.os.Build.BRAND.toLowerCase().contains("vivo")) {
//                                //注册Vivo单推服务
//                                PushClient.getInstance(MainActivity.this).bindAlias(userid1, new IPushActionListener() {
//                                    @Override
//                                    public void onStateChanged(int state) {
//                                        if (state != 0) {
//                                            Log.e(TAG, "设置别名异常[ " + state + "]");
//                                        } else {
//                                            Log.e(TAG, "设置别名成功");
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



                            //获取userId用于通知
                            String notifyUserId = sb.getString("NotifyUserId", "");
//                deleteUserQueue(); //删除队列
                            //用于判断手机是否大于7.0  大于的话开启用户通知 否则不开起通知
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                if (!TextUtils.isEmpty(notifyUserId)) {
                                    notificationChange(userid1, "0");
                                    new Thread(() ->{ basicConsume(myHandler); }).start();
                                }
                            } else {
                                Log.e(TAG, "通知不开启，小于7.0");
                            }
                        }
                    }else{}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //用户点击跳转打开第三方应用
        mNewWeb.registerHandler("showApplyParams", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Log.e(TAG, "跳转第三方: " + data);
                        Map map = JSONObject.parseObject(data, Map.class);
                        String redirectUrl = (String) map.get("redirectUrl");
                        int appLyId = (int) map.get("appId");
                        String appId = String.valueOf(appLyId);
                        if (!redirectUrl.isEmpty()) {
                            Log.e(TAG, "跳转第三方:2 " + redirectUrl);
                            if (zxIdTouTiao == null || zxIdTouTiao.isEmpty()) {
                                Log.e(TAG, "跳转第三方:3 " + redirectUrl);
                                Intent intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
                                intent.putExtra("url", redirectUrl);
                                intent.putExtra("token", usertoken1);
                                intent.putExtra("userid", userid1);
                                intent.putExtra("appId", appId);
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "跳转第三方:4" + redirectUrl);
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

        //一下两个功能一样   跳转到系统通知页面
        mNewWeb.registerHandler("NewNotifiction", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    gotoSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }); //可能不用
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

        //用户点击跳转打开咨询页面
        mNewWeb.registerHandler("showNewsParams", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Log.e(TAG, "跳转咨询页面: " + data);
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
                            Toast.makeText(MainActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //用户在订单页面点击支付跳转到支付页面
//        mNewWeb.registerHandler("CashierDeskGo", new BridgeHandler() {
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                try {
//                    if (!data.isEmpty()) {
//                        Log.e(TAG, "跳转支付页面: " + data);
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

        //用户点击跳转系统手机拨打电话界面  该接口用户自己页面拨打电话
//        mNewWeb.registerHandler("OpenPayIntent", new BridgeHandler() {
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                try {
//                    if (!data.isEmpty()) {
//                        Log.e(TAG, "打开通讯录: " + data);
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

        //用户点击跳转系统手机拨打电话界面  该接口用于第三方拨打电话
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

        //用户退出登录 清除基本存储信息
        mNewWeb.registerHandler("ClearUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    Log.i(TAG, "handler: logout");
                    ChaceSize = true;
                    //刷新缓存信息
//                    iniVersionName();
//                    if (android.os.Build.BRAND.toLowerCase().contains("xiaomi")) {
//                        //用户退出登录注销小米个推账户
//                        MiPushClient.unsetAlias(MainActivity.this, userid1, null);
//                    }
//                    if (android.os.Build.BRAND.toLowerCase().contains("vivo")) {
//                        //用户退出登录注销Vivo个推账户
//                        PushClient.getInstance(MainActivity.this).unBindAlias(userid1, new IPushActionListener() {
//                            @Override
//                            public void onStateChanged(int state) {
//                                if (state != 0) {
//                                    Log.e(TAG, "取消别名异常" + state);
//                                } else {
//                                    Log.e(TAG, "取消别名成功");
//                                }
//                            }
//                        });
//                    }
                    if (android.os.Build.BRAND.toLowerCase().contains("huawei")) {
                        PushTokenRelation("3", userid1, "2");
                        //用户退出登录注销华为个推账户
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

        //用户点击跳转手机系统浏览器界面
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

        //提示用户升级应用弹框显示清除一次缓存  点击按钮跳转应用市场
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
     * accessEquipment 接入设备 (1：iphone；2：huawei；3：mi；4：oppo；5：vivo)
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
     * 跳转到照相机
     */
    private void gotoCamera() {
        Log.d(TAG, "*****************打开相机********************");
        //创建拍照存储的图片文件
        tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/myImage/"), System.currentTimeMillis() + ".jpg");

        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
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
     * 跳转到相册
     */
    private void gotoPhoto() {
        Log.d(TAG, "*****************打开图库********************");
        //跳转到调用系统图库
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }

    /**
     * 初始页加载
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
        String apply_url = sp1.getString("apply_url", "");//从其它页面回调，并加载要回调的页面
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
//        String apply_url = sp1.getString("apply_url", "");//从其它页面回调，并加载要回调的页面
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
            Log.e(TAG, "通知不开启，小于7.0");
        }
    }

    @Override
    protected void onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new Thread(() -> basicConsume(myHandler)).start();
        } else {
            Log.e(TAG, "通知不开启，小于7.0");
        }
        super.onStop();
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

    /**
     * 获取系统版本号
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void iniVersionName() {
        totalCacheSize = CleanDataUtils.getTotalCacheSize(Objects.requireNonNull(MainActivity.this));
        try {
            //获取包管理器
            PackageManager packageManager = getPackageManager();
            //显示安装包信息
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //获取版本号
            mVersionName = packageInfo.versionName;
            Log.i(TAG, mVersionName+"   "+ totalCacheSize);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户权限
     */
    private void myRequetPermission(String[] permissionsApplication) {
        // 当API大于 23 时，才动态申请权限
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
     * 获取用户权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case VIDEO_PERMISSIONS_CODE:
                //权限请求失败
                if (grantResults.length == PERMISSIONS_APPLICATION.length) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            //弹出对话框引导用户去设置
//                            showDialog();
                            Toast.makeText(MainActivity.this, "请求权限被拒绝", Toast.LENGTH_LONG).show();
                            break;
                        } else {
                        }
                    }
                }
                break;
        }
//        if (requestCode == 1) {
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
////                    mLodingTime();
//                } else {
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//用户选择了禁止不再询问
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        builder.setTitle("permission")
//                                .setMessage("点击允许才可以使用我们的app哦")
//                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        if (mDialog != null && mDialog.isShowing()) {
//                                            mDialog.dismiss();
//                                        }
//                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                        Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
//                                        intent.setData(uri);
//                                        startActivityForResult(intent, NOT_NOTICE);
//                                    }
//                                });
//                        mDialog = builder.create();
//                        mDialog.setCanceledOnTouchOutside(false);
//                        mDialog.show();
////                        mLodingTime();
//                    } else {//选择禁止
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        builder.setTitle("permission")
//                                .setMessage("点击允许才可以使用我们的app哦")
//                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
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

    //弹出提示框
//    private void showDialog() {
//        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
//                .setTitle("提示")
//                .setMessage("若您取消权限可能会导致某些功能无法使用！！！")
//                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        goToAppSetting();
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        ClipImageActivity.goToClipActivity(this, uri);
    }

    /**
     * 获取用户权限
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 10 && resultCode == 2) {//通过请求码和返回码区分不同的返回
            String apply_url = intent.getStringExtra("apply_url");//data:后一个页面putExtra()中设置的键名
            webView(apply_url);
        }
        switch (requestCode) {
            case NOT_NOTICE:
                myRequetPermission(PERMISSIONS_APPLICATION);//由于不知道是否选择了允许所以需要再次判断
                break;
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    String realPathFromUri = getRealPathFromUri(this, uri);
                    if (realPathFromUri.endsWith(".jpg") || realPathFromUri.endsWith(".png") || realPathFromUri.endsWith(".jpeg")) {
                        gotoClipActivity(uri);
                    } else {
                        Toast.makeText(this, "选择的格式不对,请重新选择", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case REQ_CLIP_AVATAR:  //剪切图片返回
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
                    //此处后面可以将bitMap转为二进制上传后台网络
                    //......
                    Log.i(TAG, "Bearer" + " " + token1);
                    accessToken = "Bearer" + " " + token1;
                    Log.i(TAG, "accessToken---" + accessToken);
                    OkHttpClient client = new OkHttpClient();//创建okhttpClient
                    //创建body类型用于传值
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    File file = new File(cropImagePath);

                    final MediaType mediaType = MediaType.parse("image/jpeg");//创建媒房类型
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
     * Android与js交互   设置功能
     */
    class MJavaScriptInterface {
        private Context context;

        public MJavaScriptInterface(Context context) {
            this.context = context;
        }

        /**
         * 获取首页url路径
         *
         * @param interfaceUrl
         * @param appId
         * @param token
         */
        @JavascriptInterface
        public void showApplyParams(String interfaceUrl, String appId, String token) {
            Log.i("调用js的Toast", interfaceUrl);
            if (!interfaceUrl.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
                intent.putExtra("url", interfaceUrl);
                intent.putExtra("token", usertoken1);
                intent.putExtra("userid", userid1);
                intent.putExtra("appId", appId);
                Log.i(TAG, "showApplyParamstoken1---" + usertoken1 + "____" + userid1);
                startActivity(intent);
            } else {
                Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
            }

        }

        @JavascriptInterface
        public void showNewsParams(String addressUrl, String appId, String token) {
            if (!addressUrl.isEmpty()) {
                Log.i("调用js的Toast", addressUrl);
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("url", addressUrl);
                intent.putExtra("token", token1);
                startActivity(intent);
            } else {
                Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
            }

        }

        /**
         * 获取通知打开或者关闭
         */
        @JavascriptInterface
        public void NewNotifiction() {
            Log.i("radioCode--", "1321");
            gotoSet();
        }

        /**
         * 获取登录用户token
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

                //获取userId用于通知
                String notifyUserId = sb.getString("NotifyUserId", "");
                //deleteUserQueue(); //删除队列
                //用于判断手机是否大于7.0  大于的话开启用户通知 否则不开起通知
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (!TextUtils.isEmpty(notifyUserId)) {
                        notificationChange(userid1, "0");
                        new Thread(() -> basicConsume(myHandler)).start();
                    }
                } else {
                    Log.e(TAG, "通知不开启，小于7.0");
                }
            }
        }

        /**
         * 清除用户登录信息
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
     * 跳转系统通知
     */
    private void gotoSet() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", this.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", this.getPackageName());
            intent.putExtra("app_uid", this.getApplicationInfo().uid);
        } else {
            // 其他
//            getContext().getApplicationContext().getPackageName();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", this.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 获取当前通知是否打开
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
     * 收消息（从发布者那边订阅消息）
     */
    private void basicConsume(final Handler handler) {
        try {
            //连接
            Connection connection = getConnection();
            if (connection != null) {
                //通道
                channel = connection.createChannel();
                Consumer consumer = new DefaultConsumer(channel) {

                    // 获取到达的消息
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
     * 连接设置
     */
    private Connection getConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        if (rabbitMQBean != null) {
            factory.setHost(rabbitMQBean.getData().getMqAddress());//主机地址：192.168.1.105
            int MqPort = Integer.parseInt(rabbitMQBean.getData().getMqPort());
            factory.setPort(MqPort);// 端口号:5672
            factory.setUsername(rabbitMQBean.getData().getMqUser());// 用户名
            factory.setPassword(rabbitMQBean.getData().getMqPassword());// 密码
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
                && userid1.equals(notificationBean.getUserId()) && !notificationBean.getTitle().equals("推送消息数量")) {
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
            //不做处理
        }
    }

    private void createNotificationChannel() {
        //Android8.0(API26)以上需要调用下列方法，但低版本由于支持库旧，不支持调用
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = getIntent().getData(); //用户华为通知跳转  浏览器跳转应用  oppo通知跳转
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
                //test://zzy:8080/home?open=message&appid=2&appName=精益生产电子看板  用户华为通知跳转
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
                        //要执行的操作
                        mNewWeb.callHandler("PushMessageIntent", s, new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {

                            }
                        });
                    }
                }, 1000);//2秒后执行Runnable中的run方法
            }
        }

        String app_notice_list = intent.getStringExtra("APP_NOTICE_LIST");
        String xiaomiMessage = intent.getStringExtra("pushContentMessage");
        if (app_notice_list != null) {
//            webView(Constant.APP_NOTICE_LIST);
            if (app_notice_list.equals("咨询")) { //跳转到咨询页面
                webView(Constant.MyNews);
            } else if (app_notice_list.equals("消息")) {
                webView(Constant.APP_NOTICE_LIST);
                Log.e(TAG, "xiaomiMessage: "+xiaomiMessage );
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //要执行的操作
                        mNewWeb.callHandler("PushMessageIntent", xiaomiMessage, new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {

                            }
                        });
                    }
                }, 3000);//2秒后执行Runnable中的run方法
            }
        }
    }

    //获取手机唯一标识
    private String getId() {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            @SuppressLint("MissingPermission") String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
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
     * 得到全局唯一UUID
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
