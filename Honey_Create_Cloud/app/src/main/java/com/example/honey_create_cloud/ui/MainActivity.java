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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alipay.sdk.app.PayTask;
import com.example.honey_create_cloud.BuildConfig;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.MyHandlerCallBack;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.AppOrderInfo;
import com.example.honey_create_cloud.bean.BrowserBean;
import com.example.honey_create_cloud.bean.HeadPic;
import com.example.honey_create_cloud.bean.NotificationBean;
import com.example.honey_create_cloud.bean.PayBean;
import com.example.honey_create_cloud.bean.PayType;
import com.example.honey_create_cloud.bean.PictureUpload;
import com.example.honey_create_cloud.bean.WxPayBean;
import com.example.honey_create_cloud.file.CleanDataUtils;
import com.example.honey_create_cloud.util.FileUtil;
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
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import static com.example.honey_create_cloud.ui.ApplyFirstActivity.returnActivityA;
import static com.example.honey_create_cloud.ui.ApplySecondActivity.returnActivityB;
import static com.example.honey_create_cloud.ui.ApplyThirdActivity.returnActivityC;
import static com.example.honey_create_cloud.ui.ClipImageActivity.REQ_CLIP_AVATAR;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.NewWebProgressbar)
    ProgressBar mNewWebProgressbar;
    @InjectView(R.id.new_Web)
    BridgeWebView mNewWeb;
    @InjectView(R.id.web_error)
    View mWebError;


    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

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
                        mNewWeb.post(new Runnable() {
                            @SuppressLint("NewApi")
                            @Override
                            public void run() {
                                mNewWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + "1" + "\")", new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        Log.e("wangpan", "---");
                                    }
                                });
                            }
                        });
                        Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Log.e("wangpan", payResult + "");
                    } else if (TextUtils.equals(resultStatus, "4000")) {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        //此功能用于在用户支付后回调通知页面支付成功/失败
                        mNewWeb.post(new Runnable() {
                            @SuppressLint("NewApi")
                            @Override
                            public void run() {
                                mNewWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + "2" + "\")", new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        Log.e("wangpan", "---");
                                    }
                                });
                            }
                        });
                        Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        Log.e("wangpan", payResult + "");
                    } else if (TextUtils.equals(resultStatus, "8000")) {
                        Toast.makeText(MainActivity.this, "正在处理中...", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        Toast.makeText(MainActivity.this, "支付未完成,用户取消", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "5000")) {
                        Toast.makeText(MainActivity.this, "重复请求", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "支付异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case OPLOAD_IMAGE:
                    Log.e(TAG, "handleMessage: " + msg.obj);
                    newName = (String) msg.obj;
                    OkHttpClient client1 = new OkHttpClient();
//                    final FormBody formBody = new FormBody.Builder()
//                            .add("userId", userid)
//                            .add("url", newName)
//                            .build();
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
                                final String tete = "mytest";
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
                            } else {

                            }
                        }
                    });
                    break;
                case NOTIFICATION_MESSAGE: {
                    String notificationMsg = (String) msg.obj;
                    Gson gson = new Gson();
                    Intent msgIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getPackageName());//获取启动Activity
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0, msgIntent, 0);
                    NotificationBean notificationBean = gson.fromJson(notificationMsg, NotificationBean.class);
                    Log.e(TAG, "handleMessage:123123123123123 " + "--------" + notificationBean.getUserId());
                    if (!TextUtils.isEmpty(userid1) && !TextUtils.isEmpty(notificationBean.getUserId()) && userid1.equals(notificationBean.getUserId())) {
                        Log.e(TAG, "handleDelivery: " + notificationMsg + "--------");
                        ShortcutBadger.applyCount(MainActivity.this, badgeCount); //for 1.1.4+
                        Notification notification = new NotificationCompat.Builder(MainActivity.this, channel_id)
                                .setContentTitle(notificationBean.getTitle())
                                .setContentText(notificationBean.getContent())
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher_back)
                                .setDefaults(DEFAULT_ALL)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_back))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .build();
                        notificationManager.notify(badgeCount++, notification);
                        Log.e(TAG, "badgeCount: " + "" + badgeCount);
                    } else {
                        //不做处理
                    }
                }
                break;
                default:
                    break;
            }
            return false;
        }
    });

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;

    private static final String TAG = "MainActivity_TAG";

    //调用照相机返回图片文件
    private File tempFile;
    //权限
    private static final int NOT_NOTICE = 2;//如果勾选了不再询问

    private String mVersionName = "";
    private String totalCacheSize = "";
    private String clearSize = "";

    private static final String[] PERMISSIONS_APPLICATION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE};
    private static final int VIDEO_PERMISSIONS_CODE = 1;

    private MyHandlerCallBack.OnSendDataListener mOnSendDataListener;
    private String token1;
    private String userid;
    private String newName = "";
    private String accessToken;
    private String channel_id = "myChannelId";
    private String channel_name = "蜂巢制造云";
    private String description = "通知的功能";
    NotificationManager notificationManager;
    private static final int SDK_PAY_FLAG = 1;  //支付回调
    private static final int OPLOAD_IMAGE = 2;  //修改头像回调
    private static final int NOTIFICATION_MESSAGE = 3;  //用户通知

    private String usertoken1;
    private String userid1;
    private MWebChromeClient myChromeWebClient;
    private int badgeCount = 0;
    private boolean ChaceSize = true;


    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        boolean rects = ScreenAdapterUtil.hasNotchInScreen(this);
        if (rects == true) {
            //有刘海屏
            setAndroidNativeLightStatusBar(MainActivity.this, false);//白色字体
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            getWindow().setAttributes(lp);
        } else if (rects == false) {
            //无刘海屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setAndroidNativeLightStatusBar(MainActivity.this, true);//黑色字体
        }
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        initVersionName();
        myRequetPermission();

        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String id = uri.getQueryParameter("id");
            if (id != null) {
                StringBuffer sb = new StringBuffer();
                sb.append(Constant.text_url) //http://172.16.23.210:3001/home
                        .append("?id=");
                webView(sb.toString() + id);//http://172.16.23.210:3001/home?id=
            } else {
                webView(Constant.text_url);
            }
        } else {
            webView(Constant.text_url);
        }
    }

    /**
     * 初始化webview js交互
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        //Handler做为通信桥梁的作用，接收处理来自H5数据及回传Native数据的处理，当h5调用send()发送消息的时候，调用MyHandlerCallBack
        mNewWeb.setDefaultHandler(new MyHandlerCallBack(mOnSendDataListener));
        myChromeWebClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError);
        mNewWeb.setWebViewClient(new MyWebViewClient(mNewWeb));
        mNewWeb.setWebChromeClient(myChromeWebClient);
        mNewWeb.loadUrl(url);
        //回退监听
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

        //js交互接口定义
        mNewWeb.addJavascriptInterface(new MJavaScriptInterface(getApplicationContext()), "ApplyFunc");

        //有方法名的都需要注册Handler后使用  获取版本号
        mNewWeb.registerHandler("getVersionName", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (!mVersionName.isEmpty()) {
//                    mEditText.setText("通过调用Native方法接收数据：\n" + data);
                    function.onCallBack("V" + mVersionName);
                }
            }
        });

        //初始缓存
        mNewWeb.registerHandler("getCache", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if(ChaceSize == true){
                    if (!totalCacheSize.isEmpty()) {
                        function.onCallBack(totalCacheSize);
                    }
                }else{
                    function.onCallBack("0.00MB");
                }
            }
        });

        //用户点击后缓存
        mNewWeb.registerHandler("ClearCache", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                CleanDataUtils.clearAllCache(Objects.requireNonNull(MainActivity.this));
                clearSize = CleanDataUtils.getTotalCacheSize(Objects.requireNonNull(MainActivity.this));
                if (!clearSize.isEmpty()) {
                    ChaceSize = false;
                    Log.e(TAG, "handler: " + clearSize);
                    function.onCallBack(clearSize);
                }
            }
        });

        //拍照
        mNewWeb.registerHandler("getTakeCamera", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
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
            }
        });

        //相册
        mNewWeb.registerHandler("getPhotoAlbum", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
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
            }
        });

        /**
         * 用户加载初始通知
         */
        mNewWeb.registerHandler("getNotification", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                boolean enabled = isNotificationEnabled(MainActivity.this);
                if (enabled == true) {
                    function.onCallBack("1");
                } else {
                    function.onCallBack("2");
                }
            }
        });

        /**
         * 获取用户基本信息
         */
        mNewWeb.registerHandler("getUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                String userInfo = sb.getString("userInfo", "");
                Log.e("wangpan", userInfo);
                if (!userInfo.isEmpty()) {
                    function.onCallBack(sb.getString("userInfo", ""));
                } else {

                }
            }
        });
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
         * 获取用户登录信息,并存储，用于支付
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


        //联系客服  打开通讯录
        @JavascriptInterface
        public void OpenPayIntent(String intentOpenPay) {
            Log.e(TAG, "OpenPayIntent: "+intentOpenPay );
//            int i = Integer.parseInt(intentOpenPay);
//            String s = String.valueOf(intentOpenPay);
//            Log.e(TAG, "OpenPayIntent: "+s);
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + intentOpenPay));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        /**
         * 获取第三放url路径
         *
         * @param interfaceUrl
         * @param appId
         */
        @JavascriptInterface
        public void showApplyParams(String interfaceUrl, String appId) {
            if (!interfaceUrl.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
                intent.putExtra("url", interfaceUrl);
                intent.putExtra("token", usertoken1);
                intent.putExtra("userid", userid1);
                intent.putExtra("appId", appId);
                startActivity(intent);
            } else {
                Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * 获取跳转头条url
         */
        @JavascriptInterface
        public void showNewsParams(String addressUrl, String appId, String token) {
            if (!addressUrl.isEmpty()) {
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
            gotoSet();
        }

        /**
         * 获取登录用户token  用于第三方页面悬浮按钮接口查询
         */
        @JavascriptInterface
        public void getToken(String usertoken, String userid) {
            if (!usertoken.isEmpty()) {
                usertoken1 = usertoken;
                userid1 = userid;
                SharedPreferences sb = context.getSharedPreferences("NotificationUserId", MODE_PRIVATE);
                SharedPreferences.Editor edit = sb.edit();
                edit.putString("NotifyUserId", userid);
                edit.commit();

                //获取userId用于通知
                String notifyUserId = sb.getString("NotifyUserId", "");
                Log.e(TAG, "getToken: " + notifyUserId);
                if (!TextUtils.isEmpty(notifyUserId)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            basicConsume(myHandler);
                        }
                    }).start();
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

        /**
         * 返回关闭支付页面
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
        public void CashierDeskGo(String userId, String orderNo, String outTradeNo){
            Log.e(TAG, "CashierDeskGo: "+userId+"--"+orderNo+"--"+outTradeNo+"--"+usertoken1 );
            Intent intent = new Intent(MainActivity.this,IntentOpenActivity.class);
            intent.putExtra("userId",userId);
            intent.putExtra("orderNo",orderNo);
            intent.putExtra("outTradeNo",outTradeNo);
            intent.putExtra("token",usertoken1);
            startActivity(intent);
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
                .addHeader("Authorization", "Bearer " + usertoken1)
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
                .addHeader("Authorization", "Bearer " + usertoken1)
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
                .addHeader("Authorization", "Bearer " + usertoken1)
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
                            PayTask alipay = new PayTask(MainActivity.this);
                            Map<String, String> result = alipay.payV2(orderInfo, true);

                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            myHandler.sendMessage(msg);
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
                .addHeader("Authorization", "Bearer " + usertoken1)
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

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onMessageEvent(String event) {
        if (event.equals("支付成功")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            mNewWeb.post(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    mNewWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + "1" + "\")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.e("wangpan", "---");
                        }
                    });
                }
            });
        } else if (event.equals("支付失败")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            mNewWeb.post(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    mNewWeb.evaluateJavascript("window.sdk.paymentFeedback(\"" + "2" + "\")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.e("wangpan", "---");
                        }
                    });
                }
            });
        }else if (event.equals("打开应用")) {
            webView(Constant.apply_url);
        }else if(event.equals("打开首页")){
            Log.e(TAG, "onMessageEvent:asdasdf ");
            webView(Constant.text_url);
        }
    }

    /**
     * 收消息（从发布者那边订阅消息）
     */
    private void basicConsume(final Handler handler) {
        String userId = "3B9B1E217F86D5E493FCE81A5B800770";
        Log.e(TAG, "run:1 ");
        try {
            //连接
            Connection connection = getConnection();
            if (connection != null) {
                Log.e(TAG, "run:2 ");
                //通道
                final Channel channel = connection.createChannel();
                AMQP.Queue.DeclareOk declareOk = channel.queueDeclare("app.notice.queue", true, false, false, null);
                channel.queueBind(declareOk.getQueue(), "app.notice.exchange", "notice.key");
                DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {

                    // 获取到达的消息
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException {
                        super.handleDelivery(consumerTag, envelope, properties, body);
                        String receiveMsg = new String(body, "UTF-8");
                        Log.e(TAG, "handleDelivery: " + receiveMsg);
                        Message message = new Message();
                        message.what = NOTIFICATION_MESSAGE;
                        message.obj = receiveMsg;
                        handler.sendMessage(message);
                    }
                };
                channel.basicConsume("app.notice.queue", true, defaultConsumer);
                Log.e(TAG, "run: 3");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接设置
     */
    private Connection getConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("119.3.28.24");
        factory.setPort(5672);
        factory.setUsername("honeycomb");
        factory.setPassword("honeycomb");
        factory.setVirtualHost("/");
        try {
            return factory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 跳转到照相机
     */
    private void gotoCamera() {
        //	获取图片沙盒文件夹
        File dPictures = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //图片名称
        String mFileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        //图片路径
        String mFilePath = dPictures.getAbsolutePath() + "/" + mFileName;
        //创建拍照存储的图片文件
//        tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"), System.currentTimeMillis() + ".jpg");
        tempFile = new File(mFilePath);
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * 跳转到相册
     */
    private void gotoPhoto() {
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
        Log.e(TAG, "onResume");
        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onResume");
            }
        });
        boolean notificationEnabled = isNotificationEnabled(this);

        Log.e(TAG, "badgeCount: " + "" + badgeCount);
        Log.e(TAG, "onResume: " + notificationEnabled);
        if (notificationEnabled == true) {
//            notificationChange(userid1,"1");
        } else {
//            notificationChange(userid1,"-1");
        }
        super.onResume();
    }

    private void notificationChange(String userid1, String s) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(Constant.NOTICE_OPEN_SWITCH + userid1 + "/" + s)
                .addHeader("Authorization", "Bearer " + usertoken1)
                .put(null)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    String string = response.body().string();
                    Log.e(TAG, "notificationChange: " + string);

                } else {

                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        if (returnActivityA == true) {
            returnActivityA = false;
            Intent intent = new Intent();
            intent.setClass(this, ApplyFirstActivity.class);
            startActivity(intent);
        } else if (returnActivityB == true) {
            returnActivityB = false;
            Intent intent = new Intent();
            intent.setClass(this, ApplySecondActivity.class);
            startActivity(intent);
        } else if (returnActivityC == true) {
            returnActivityC = false;
            Intent intent = new Intent();
            intent.setClass(this, ApplyThirdActivity.class);
            startActivity(intent);
        }

        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onStart");
            }
        });
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestart() {
        mNewWeb.reload();
        Log.e(TAG, "onRestart");
        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onRestart");
            }
        });
        SharedPreferences sb = getSharedPreferences("NotificationUserId", MODE_PRIVATE);
        String notifyUserId = sb.getString("NotifyUserId", "");
        if (!TextUtils.isEmpty(notifyUserId)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    basicConsume(myHandler);
                }
            }).start();
        }

        SharedPreferences sp1 = getSharedPreferences("apply_urlSafe",MODE_PRIVATE);
        String apply_url = sp1.getString("apply_url", "");//从其它页面回调，并加载要回调的页面
        if (!TextUtils.isEmpty(apply_url)){
            Log.e(TAG, "123: "+apply_url );
            webView(apply_url);
        }
        SharedPreferences.Editor edit = sp1.edit();
        edit.clear();
        edit.commit();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ShortcutBadger.applyCount(MainActivity.this, badgeCount); //for 1.1.4+
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
    private void initVersionName() {
        totalCacheSize = CleanDataUtils.getTotalCacheSize(Objects.requireNonNull(MainActivity.this));
        try {
            //获取包管理器
            PackageManager packageManager = getPackageManager();
            //显示安装包信息
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //获取版本号
            mVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户权限
     */
    private void myRequetPermission() {
        // 当API大于 23 时，才动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_APPLICATION, VIDEO_PERMISSIONS_CODE);
        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, PERMISSIONS_APPLICATION, 1);
//        } else {
//            mLodingTime();
//        }
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
                            showDialog();
                            Toast.makeText(MainActivity.this, "请求权限被拒绝", Toast.LENGTH_LONG).show();
                            break;
                        } else {
                        }
                    }
                }
                break;
        }
    }

    //弹出提示框
    private void showDialog() {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("若您取消权限可能会导致某些功能无法使用！！！")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        goToAppSetting();
                    }
                })
                .setCancelable(false)
                .show();
    }

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
     * 获取用户权限
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == 10 && resultCode == 2){//通过请求码和返回码区分不同的返回
            String apply_url = intent.getStringExtra("apply_url");//data:后一个页面putExtra()中设置的键名
            Log.e(TAG, "onActivityResult: "+apply_url);
            webView(apply_url);
        }
        switch (requestCode) {
            case NOT_NOTICE:
                myRequetPermission();//由于不知道是否选择了允许所以需要再次判断
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
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String date = simpleDateFormat.format(new Date());
                    FileUtil.saveBitmapToSDCard(bitMap, "123");
                    //此处后面可以将bitMap转为二进制上传后台网络

                    accessToken = "Bearer" + " " + token1;
                    OkHttpClient client = new OkHttpClient();//创建okhttpClient
                    //创建body类型用于传值
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    File file = new File(cropImagePath);

                    final MediaType mediaType = MediaType.parse("image/jpeg");//创建媒房类型
                    builder.addFormDataPart("fileObjs", file.getName(), RequestBody.create(mediaType, file));
                    builder.addFormDataPart("fileNames", "");
                    builder.addFormDataPart("bucketName", "njdeveloptest");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

