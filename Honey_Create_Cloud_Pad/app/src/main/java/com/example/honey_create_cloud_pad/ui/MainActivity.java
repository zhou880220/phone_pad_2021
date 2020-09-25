package com.example.honey_create_cloud_pad.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONObject;
import com.example.honey_create_cloud_pad.BuildConfig;
import com.example.honey_create_cloud_pad.Constant;
import com.example.honey_create_cloud_pad.MyHandlerCallBack;
import com.example.honey_create_cloud_pad.R;
import com.example.honey_create_cloud_pad.bean.BrowserBean;
import com.example.honey_create_cloud_pad.bean.HeadPic;
import com.example.honey_create_cloud_pad.bean.PictureUpload;
import com.example.honey_create_cloud_pad.file.CleanDataUtils;
import com.example.honey_create_cloud_pad.util.FileUtil;
import com.example.honey_create_cloud_pad.view.AnimationView;
import com.example.honey_create_cloud_pad.webclient.MWebChromeClient;
import com.example.honey_create_cloud_pad.webclient.MWebViewClient;
import com.example.honey_create_cloud_pad.webclient.MyWebViewClient;
import com.example.honey_create_cloud_pad.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.honey_create_cloud_pad.ui.ClipImageActivity.REQ_CLIP_AVATAR;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.newwebprogressbar)
    ProgressBar mNewwebprogressbar;
    @InjectView(R.id.new_Web)
    BridgeWebView mNewWeb;
    @InjectView(R.id.loading_page)
    View mLoadingPage;
    @InjectView(R.id.web_error)
    View mWebError;

    private static final String[] PERMISSIONS_APPLICATION = { //应用中心授权
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE};

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
                final FormBody formBody = new FormBody.Builder()
                        .add("userId", userid)
                        .add("url", newName)
                        .build();
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
                            Log.i(TAG, "msg1---" + msg1);
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
                        }
                    }
                });
            }
            return false;
        }
    });
    private String usertoken1;
    private String userid1;
    private MWebChromeClient myChromeWebClient;
    private String backUrl;
    private int back;
    private String myOrder;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        iniVersionName();
        Uri uri = getIntent().getData();
        if (uri != null) {
            String id = uri.getQueryParameter("thirdId");
            if (id != null) {
                Intent intent = new Intent(this, NewsActivity.class);
                intent.putExtra("url", id);
                startActivity(intent);
            }
        }
        myRequetPermission();
        webView(Constant.text_url);
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
        String userAgentString = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgentString + "; application-center");

        WebViewSetting.initweb(webSettings);
        //Handler做为通信桥梁的作用，接收处理来自H5数据及回传Native数据的处理，当h5调用send()发送消息的时候，调用MyHandlerCallBack
        mNewWeb.setDefaultHandler(new MyHandlerCallBack(mOnSendDataListener));
        myChromeWebClient = new MWebChromeClient(this, mNewwebprogressbar, mWebError);
        mNewWeb.setWebChromeClient(myChromeWebClient);
        MyWebViewClient myWebViewClient = new MyWebViewClient(mNewWeb, mWebError);
        myWebViewClient.setOnCityClickListener(new MyWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {  //动态监听页面加载链接
                myOrder = name;
                Log.e(TAG, "onCityClick: " + name);
//                if (name.equals(Constant.login_url)) {
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
        });
        mNewWeb.setWebViewClient(myWebViewClient);
        mNewWeb.loadUrl(url);

        //js交互接口定义
        mNewWeb.addJavascriptInterface(new MJavaScriptInterface(getApplicationContext()), "ApplyFunc");

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

        //初始缓存
        mNewWeb.registerHandler("getCache", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i("totalCacheSize", totalCacheSize);
                if (!totalCacheSize.isEmpty()) {
                    function.onCallBack(totalCacheSize);
                }
            }
        });

        //用户点击后缓存
        mNewWeb.registerHandler("ClearCache", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                CleanDataUtils.clearAllCache(Objects.<Context>requireNonNull(MainActivity.this));
                clearSize = CleanDataUtils.getTotalCacheSize(Objects.<Context>requireNonNull(MainActivity.this));
                if (!clearSize.isEmpty()) {
                    function.onCallBack(clearSize);
                }
            }
        });

        //拍照
        mNewWeb.registerHandler("getTakeCamera", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                if (!data.isEmpty()){
                    String replace1 = data.replace("\"", "");
                    String replace2 = replace1.replace("token:", "");
                    String replace3 = replace2.replace("{", "");
                    String replace4 = replace3.replace("}", "");
                    String[] s = replace4.split(" ");
                    Log.i(TAG, "replace---" + replace1);
                    token1 = s[0];
                    userid = s[1];
                    gotoCamera();
                }else{

                }
            }
        });

        //相册
        mNewWeb.registerHandler("getPhotoAlbum", new BridgeHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                if (!data.isEmpty()){
                    String replace1 = data.replace("\"", "");
                    String replace2 = replace1.replace("token:", "");
                    String replace3 = replace2.replace("{", "");
                    String replace4 = replace3.replace("}", "");
                    String[] s = replace4.split(" ");
                    Log.i(TAG, "replace---" + replace1);
                    token1 = s[0];
                    userid = s[1];
                    gotoPhoto();
                }else{

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
                Log.i("enabled_first", enabled + "");
                if (enabled == true) {
                    function.onCallBack("1");
                } else {
                    function.onCallBack("2");
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
                        Log.e(TAG, "用户登录信息: " + usertoken + "---" + userID);
                        if (!usertoken.isEmpty()) {
                            usertoken1 = usertoken;
                            userid1 = userID;

                            SharedPreferences sb = MainActivity.this.getSharedPreferences("NotificationUserId", MODE_PRIVATE);
                            SharedPreferences.Editor edit = sb.edit();
                            edit.putString("NotifyUserId", usertoken1);
                            edit.commit();

                            //获取userId用于通知
                            String notifyUserId = sb.getString("NotifyUserId", "");
//                deleteUserQueue(); //删除队列
                            if (!TextUtils.isEmpty(notifyUserId)) {
                                notificationChange(userid1, "0");
//                                new Thread(() -> basicConsume(myHandler)).start();
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
                            Intent intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
                            intent.putExtra("url", redirectUrl);
                            intent.putExtra("token", usertoken1);
                            intent.putExtra("userid", userid1);
                            intent.putExtra("appId", appId);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {

            }
        });
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
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
        SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
        String apply_url = sp1.getString("apply_url", "");//从其它页面回调，并加载要回调的页面
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
        super.onRestart();
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
            Log.i("mVersionName_1", mVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户权限
     */
    private void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_APPLICATION, 1);
        } else {
            mLodingTime();
        }
    }

    /**
     * 获取用户权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    mLodingTime();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//用户选择了禁止不再询问
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("permission")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mDialog != null && mDialog.isShowing()) {
                                            mDialog.dismiss();
                                        }
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
                                        intent.setData(uri);
                                        startActivityForResult(intent, NOT_NOTICE);
                                    }
                                });
                        mDialog = builder.create();
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();
                        mLodingTime();
                    } else {//选择禁止
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("permission")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (alertDialog != null && alertDialog.isShowing()) {
                                            alertDialog.dismiss();
                                        }
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                                    }
                                });
                        alertDialog = builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                }
            }
        }
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
        if (requestCode == NOT_NOTICE) {
            myRequetPermission();//由于不知道是否选择了允许所以需要再次判断
        }
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
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
            usertoken1 = usertoken;
            userid1 = userid;
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
}
