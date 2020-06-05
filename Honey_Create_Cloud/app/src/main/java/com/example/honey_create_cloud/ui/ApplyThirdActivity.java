package com.example.honey_create_cloud.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.honey_create_cloud.BuildConfig;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.adapter.MyContactAdapter;
import com.example.honey_create_cloud.bean.BrowserBean;
import com.example.honey_create_cloud.bean.PictureUpload;
import com.example.honey_create_cloud.bean.RecentlyApps;
import com.example.honey_create_cloud.bean.TakePhoneBean;
import com.example.honey_create_cloud.recorder.AudioRecorderButton;
import com.example.honey_create_cloud.util.FileUtil;
import com.example.honey_create_cloud.util.ScreenAdapterUtil;
import com.example.honey_create_cloud.util.ShareSDK_Web;
import com.example.honey_create_cloud.util.SystemUtil;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MyWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.yzq.zxinglibrary.android.CaptureActivity;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.honey_create_cloud.ui.ApplyFirstActivity.returnActivityA;
import static com.example.honey_create_cloud.ui.ApplySecondActivity.returnActivityB;
import static com.example.honey_create_cloud.ui.MainActivity.getRealPathFromUri;

public class ApplyThirdActivity extends AppCompatActivity {
    @InjectView(R.id.NewWebProgressbar)
    ProgressBar mNewWebProgressbar;
    @InjectView(R.id.new_Web3)
    BridgeWebView mNewWeb;
    @InjectView(R.id.web_error)
    View mWebError;
    @InjectView(R.id.glide_gif)
    View mLoadingPage;
    @InjectView(R.id.reload_tv)
    TextView mReloadTv;
    @InjectView(R.id.grid_popup)
    RecyclerView mGridPopup;
    @InjectView(R.id.tv_publish)
    ImageView mTvPublish;
    @InjectView(R.id.tv_myPublish)
    ImageView mTvMyPublish;
    @InjectView(R.id.tv_relation)
    ImageView mTvRelation;
    @InjectView(R.id.ll_popup)
    LinearLayout mLlPopup;
    @InjectView(R.id.iv_collection_me)
    ImageView mIvCollectionMe;
    @InjectView(R.id.tt_course_none)
    TextView mTtCourseNone;
    @InjectView(R.id.ll_course_none)
    LinearLayout mLlCourseNone;
    @InjectView(R.id.fab_more)
    ImageView mFabMore;
    @InjectView(R.id.show_dismiss)
    RelativeLayout mShowDismiss;
    @InjectView(R.id.dimiss_popup)
    RelativeLayout mDimissPopup;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case OPLOAD_IMAGE: {
                    Log.e(TAG, "handleMessage: " + msg.obj);
                    String newName = (String) msg.obj;
                    OkHttpClient client1 = new OkHttpClient();
                    final FormBody formBody = new FormBody.Builder()
                            .add("fileNames", userid)
                            .add("bucketName", "njdeveloptest")
                            .add("folderName", "menu")
                            .build();
                    Request request = new Request.Builder()
                            .addHeader("Authorization", accessToken)
                            .url(Constant.TAKE_PHOTO)
                            .post(formBody)
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
                            Log.e(TAG, "onResponse: " + string);
                            TakePhoneBean takePhoneBean = gson.fromJson(string, TakePhoneBean.class);
                            List<TakePhoneBean.DataBean> data = takePhoneBean.getData();
                            String fileUrl1 = data.get(0).getFileUrl();
                            String imageurl = newName + "&&" + fileUrl1;
                            Log.e(TAG, "onResponse: " + "---" + imageurl);
                            mNewWeb.post(new Runnable() {
                                @Override
                                public void run() {
                                    mNewWeb.evaluateJavascript("window.sdk.AlreadyPhoto(\"" + imageurl + "\")", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {

                                        }
                                    });
                                }
                            });
                        }
                    });
                    break;
                }
            }
            return false;
        }
    });

    private static final String TAG = "ApplyThirdActivity_TAG";
    private MyContactAdapter adapter;
    private boolean isShow;
    private String token;
    private String url;
    private String userid;
    private List<RecentlyApps.DataBean> data;
    private MWebChromeClient mWebChromeClient;
    public static boolean returnActivityC;
    private String appId;
    private int REQUEST_CODE_SCAN = 1;
    //状态
    private static final int STATE_NORMAL = 1;
    //当前状态
    private int mCurState = STATE_NORMAL;

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //修改头像回调
    private static final int OPLOAD_IMAGE = 2;

    //调用照相机返回图片文件
    private File tempFile;

    private String accessToken;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        boolean rects = ScreenAdapterUtil.hasNotchInScreen(this);
        if (rects == true) {
            //有刘海屏
            setAndroidNativeLightStatusBar(ApplyThirdActivity.this, false);//白色字体
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            getWindow().setAttributes(lp);
        } else if (rects == false) {
            //无刘海屏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            setAndroidNativeLightStatusBar(ApplyThirdActivity.this, true);//黑色字体
        }
        setContentView(R.layout.activity_apply_third);
        returnActivityC = true;
        ButterKnife.inject(this);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        token = intent.getStringExtra("token");
        userid = intent.getStringExtra("userid");
        appId = intent.getStringExtra("appId");
        webView(url);
        mLodingTime();
        intentOkhttp();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refreshPay");
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
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
        webSettings.setUserAgentString("application-center");
        if (webSettings != null) {
            WebViewSetting.initweb(webSettings);
        }
        mNewWeb.loadUrl(url);
        //js交互接口定义
        mNewWeb.addJavascriptInterface(new MJavaScriptInterface(getApplicationContext()), "ApplyFunc");
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
         * 获取版本号
         */
        mNewWeb.registerHandler("getSystemVersion", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                function.onCallBack(SystemUtil.getSystemVersion());
            }
        });
        /**
         * 获取手机唯一标识符
         */
        mNewWeb.registerHandler("getIdentifier", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String imei = SystemUtil.getIMEI(ApplyThirdActivity.this);
                function.onCallBack(imei);
            }
        });
        /**
         * 读取第三方存储信息
         */
        mNewWeb.registerHandler("getStoreData", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                SharedPreferences sb = getSharedPreferences(appId, MODE_PRIVATE);
                String storeData = sb.getString("storeData", "");
                Log.e("wangpan", storeData);
                function.onCallBack(storeData);
            }
        });
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
                    Toast.makeText(ApplyThirdActivity.this, "获取用户数据异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /**
         * 三方应用拍照
         */
        mNewWeb.registerHandler("setApplyCamera", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (!data.isEmpty()) {
                    Log.e(TAG, "123: " + data);
                    gotoCamera();
                }
            }
        });
        /**
         * 三方应用相册
         */
        mNewWeb.registerHandler("setApplyPhotoAlbum", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (!data.isEmpty()) {
                    Log.e(TAG, "123: " + data);
                    gotoPhoto();
                }
            }
        });
    }

    class MJavaScriptInterface implements View.OnClickListener {
        private Context context;
        private ShareSDK_Web shareSDK_web;
        private PopupWindow popupWindow;

        public MJavaScriptInterface(Context context) {
            this.context = context;
        }

        //联系客服  打开通讯录
        @JavascriptInterface
        public void OpenPayIntent(String intentOpenPay) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + intentOpenPay));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        //跳转支付页面
        @JavascriptInterface
        public void purchaseOfEntry(String purchaseOfEntry) {
            if (!purchaseOfEntry.isEmpty()) {
                Intent intent = new Intent(ApplyThirdActivity.this, IntentOpenActivity.class);
                intent.putExtra("purchaseOfEntry", purchaseOfEntry);
                intent.putExtra("appId", appId);
                returnActivityC = true;
                startActivity(intent);
            }
        }

        //打开系统通知界面
        @JavascriptInterface
        public void openNotification() {
            gotoSet();
        }

        //用户取消权限
        @JavascriptInterface
        public void cancelAuthorization() {
            returnActivityC = true;
            finish();
        }

        //分享功能
        @JavascriptInterface
        public void shareSDKData(String shareData) {
            Log.e("wangpan", shareData);
            //集成分享类
            shareSDK_web = new ShareSDK_Web(ApplyThirdActivity.this, shareData);
            View centerView = LayoutInflater.from(ApplyThirdActivity.this).inflate(R.layout.popupwindow, null);
            popupWindow = new PopupWindow(centerView, ViewGroup.LayoutParams.MATCH_PARENT,
                    400);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(centerView, Gravity.BOTTOM, 0, 0);

            View mWeChat = centerView.findViewById(R.id.wechat);
            View mWeChatMoments = centerView.findViewById(R.id.wechatmoments);
            View mSinaWeiBo = centerView.findViewById(R.id.sinaweibo);
            View mQq = centerView.findViewById(R.id.qq);
            View mQZone = centerView.findViewById(R.id.qzone);
            TextView mDismiss = centerView.findViewById(R.id.popup_dismiss);

            mWeChat.setOnClickListener(this);
            mWeChatMoments.setOnClickListener(this);
            mSinaWeiBo.setOnClickListener(this);
            mQq.setOnClickListener(this);
            mQZone.setOnClickListener(this);
            mDismiss.setOnClickListener(this);
        }

        //存储本地数据
        @JavascriptInterface
        public void setStoreData(String storeData) {
            Log.e("wangpan", appId);
            SharedPreferences sp = context.getSharedPreferences(appId, MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("storeData", storeData);
            edit.commit();
        }

        //扫一扫
        @JavascriptInterface
        public void startIntentZing() {
            Intent intent = new Intent(ApplyThirdActivity.this, CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SCAN);
        }

        //启动本地浏览器
        @JavascriptInterface
        public void intentBrowser(String browser) {
            Log.e("wangpan", browser);
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

        @JavascriptInterface
        public void openVoice() {
            Log.e(TAG, "handler: 11");
            View centerView = LayoutInflater.from(ApplyThirdActivity.this).inflate(R.layout.recorder_layout, null);
            PopupWindow popupWindow = new PopupWindow(centerView, ViewGroup.LayoutParams.MATCH_PARENT, 290);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(centerView, Gravity.BOTTOM, 0, 0);

            AudioRecorderButton mAudioRecorderButton = centerView.findViewById(R.id.id_recorder_button);
            mAudioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
                @Override
                public void onFinish(float seconds, String filePath) {
                    String s = tobase64(filePath);
                    mNewWeb.post(new Runnable() {
                        @SuppressLint("NewApi")
                        @Override
                        public void run() {
                            mNewWeb.evaluateJavascript("window.sdk.openVoice(\"" + s + "\")", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    Log.e("wangpan", "---");
                                }
                            });
                        }
                    });
                }
            });
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.wechat:
                    shareSDK_web.WechatshowShare();
                    popupWindow.dismiss();
                    break;
                case R.id.wechatmoments:
                    shareSDK_web.WechatMomentsshowShare();
                    popupWindow.dismiss();
                    break;
                case R.id.sinaweibo:
                    shareSDK_web.SinaweiboshowShare();
                    popupWindow.dismiss();
                    break;
                case R.id.qq:
                    shareSDK_web.QQshowShare();
                    popupWindow.dismiss();
                    break;
                case R.id.qzone:
                    shareSDK_web.QZoneshowShare();
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
     * 跳转到相册
     */
    private void gotoPhoto() {
        //跳转到调用系统图库
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
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

    public String tobase64(String url) {
        try {
            File file = new File(url);
            // 下载网络文件
            int bytesum = 0;
            int byteread = 0;
            InputStream inStream = new FileInputStream(file);
            int size = inStream.available();
            byte[] buffer = new byte[size];
            while ((byteread = inStream.read(buffer)) != -1) {
                inStream.read(buffer);
                inStream.close();
                byte[] bytes = Base64.encodeBase64(buffer);
                String str = new String(bytes);
                if (str != null) {
                    str = str.replaceAll(System.getProperty("line.separator"), "");
                    str = str.replaceAll("=", "");
                    str = str.replaceAll(" ", "");
                }
                return str;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取悬浮窗接口信息
     */
    private void intentOkhttp() {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(Constant.Apply_Details + userid)
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
                    RecentlyApps recentlyApps = gson.fromJson(string, RecentlyApps.class);
                    data = recentlyApps.getData();
                    String s = recentlyApps.toString();
                } else {
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫一扫二维码返回
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String stringExtra = data.getStringExtra(com.yzq.zxinglibrary.common.Constant.CODED_CONTENT);
                if (stringExtra.startsWith("http:") || stringExtra.startsWith("https:")) {
                    Intent intent = new Intent(ApplyThirdActivity.this, ZingWebActivity.class);
                    intent.putExtra("url", stringExtra);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "解析失败，换个图片试试", Toast.LENGTH_SHORT).show();
                }
            }
        }

        switch (requestCode) {
            case REQUEST_CAPTURE:
                if (resultCode == RESULT_OK) {//调用系统相机返回
                    Uri uri = Uri.fromFile(tempFile);
                    Log.e(TAG, "onActivityResult: " + uri);
                    takePhoneUrl(uri);
//                    gotoClipActivity(Uri.fromFile(tempFile));
                }else if(resultCode == RESULT_CANCELED){
                    mNewWeb.post(new Runnable() {
                        @Override
                        public void run() {
                            mNewWeb.evaluateJavascript("window.sdk.AlreadyPhoto(\"" + "取消" + "\")", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    Log.e(TAG, "onReceiveValue: 取消" );
                                }
                            });
                        }
                    });
                }
                break;
            case REQUEST_PICK://调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String realPathFromUri = getRealPathFromUri(this, uri);
                    if (realPathFromUri.endsWith(".jpg") || realPathFromUri.endsWith(".png") || realPathFromUri.endsWith(".jpeg")) {
//                        gotoClipActivity(uri);
                        Log.e(TAG, "onActivityResult: " + uri);
                        takePhoneUrl(uri);
                    } else {
                        Toast.makeText(this, "选择的格式不对,请重新选择", Toast.LENGTH_SHORT).show();
                    }
                }else if(resultCode == RESULT_CANCELED){
                    mNewWeb.post(new Runnable() {
                        @Override
                        public void run() {
                            mNewWeb.evaluateJavascript("window.sdk.AlreadyPhoto(\"" + "取消" + "\")", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    Log.e(TAG, "onReceiveValue: 取消" );
                                }
                            });
                        }
                    });
                }
                break;
        }
    }

    private void takePhoneUrl(Uri uri) {
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

        accessToken = "Bearer" + " " + token;
        OkHttpClient client = new OkHttpClient();//创建okhttpClient
        //创建body类型用于传值
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = new File(cropImagePath);

        final MediaType mediaType = MediaType.parse("image/jpeg");//创建媒房类型
        builder.addFormDataPart("fileObjs", file.getName(), RequestBody.create(mediaType, file));
        builder.addFormDataPart("fileNames", "");
        builder.addFormDataPart("bucketName", "njdeveloptest");
        builder.addFormDataPart("folderName", "menu");
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
                    Message message = new Message();
                    message.what = OPLOAD_IMAGE;
                    message.obj = data.get(0).getNewName();
                    handler.sendMessage(message);
                } else {

                }
            }
        });
    }

    //获取手机唯一标识
    private String getId() {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
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
                deviceId.append("id");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("id").append(getUUID());
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

    // broadcast receiver
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.refreshPay")) {
                mNewWeb.evaluateJavascript("window.sdk.noticeOfPayment()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
        }
    };

    /**
     * 菜单展开按钮功能
     *
     * @param v
     */
    @OnClick({R.id.tv_publish, R.id.tv_myPublish, R.id.tv_relation, R.id.fab_more})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_publish:
                mTvPublish.setBackgroundResource(R.mipmap.floatinghomechange);
                mTvMyPublish.setBackgroundResource(R.mipmap.floatingapply);
                mTvRelation.setBackgroundResource(R.mipmap.floatingapp);
                returnActivityA = false;
                returnActivityB = false;
                returnActivityC = false;
                SharedPreferences sp1 = this.getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                SharedPreferences.Editor edit1 = sp1.edit();
                edit1.putString("apply_url", Constant.text_url);
                edit1.commit();
                Intent intent = new Intent(ApplyThirdActivity.this, MainActivity.class);
                intent.putExtra("apply_url", Constant.text_url);
                startActivity(intent);
                break;
            case R.id.tv_myPublish:
                mTvPublish.setBackgroundResource(R.mipmap.floatinghome);
                mTvMyPublish.setBackgroundResource(R.mipmap.floatingapplychange);
                mTvRelation.setBackgroundResource(R.mipmap.floatingapp);
                returnActivityA = false;
                returnActivityB = false;
                returnActivityC = false;
                SharedPreferences sp = this.getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("apply_url", Constant.apply_url);
                edit.commit();
                Intent intent1 = new Intent(ApplyThirdActivity.this, MainActivity.class);
                intent1.putExtra("apply_url", Constant.apply_url);
                startActivity(intent1);
                break;
            case R.id.tv_relation:
                mTvPublish.setBackgroundResource(R.mipmap.floatinghome);
                mTvMyPublish.setBackgroundResource(R.mipmap.floatingapply);
                mTvRelation.setBackgroundResource(R.mipmap.floatingappchange);
                mShowDismiss.setVisibility(View.VISIBLE);
                pagerView();
                adapter.setOnClosePopupListener(new MyContactAdapter.OnClosePopupListener() {
                    @Override
                    public void onClosePopupClick(String name) {
                        if (name.equals("关闭")) {
                            mShowDismiss.setVisibility(View.GONE);
                            switchPopup();
                        }
                    }
                });

                break;
            case R.id.fab_more:
                mDimissPopup.setVisibility(View.VISIBLE);
                switchPopup();
                break;
            case R.id.dimiss_popup:
                mDimissPopup.setVisibility(View.GONE);
                switchPopup();
                break;
            case R.id.show_dismiss:
                mShowDismiss.setVisibility(View.GONE);
                switchPopup();
                break;
        }
    }

    /**
     * 开合popup
     */
    private void switchPopup() {
        if (!isShow) {
            isShow = true;
//            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(fabMore, "rotation", 45f);
//            objectAnimator.setDuration(300);
//            objectAnimator.start();

            mLlPopup.setVisibility(View.VISIBLE);
            ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1.0f, 1.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(300);
            mLlPopup.startAnimation(scaleAnimation);

            if (mLlCourseNone.getVisibility() == View.VISIBLE) {
                float fY = mFabMore.getY();
                float tY = mTtCourseNone.getY();
                if (250 > fY - tY) {
                    mTtCourseNone.setText("");
                }
            }
        } else {
            isShow = false;
//            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(fabMore, "rotation", 0);
//            objectAnimator.setDuration(300);
//            objectAnimator.start();

            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(300);
            mLlPopup.startAnimation(scaleAnimation);
            mLlPopup.setVisibility(View.GONE);

            if (mLlCourseNone.getVisibility() == View.VISIBLE) {
                float fY = mFabMore.getY();
                float tY = mTtCourseNone.getY();
                if (250 > fY - tY) {
                    mTtCourseNone.setText("暂时没有内容哦");
                }
            }
        }
    }

    /**
     * 悬浮框
     */
    private void pagerView() {
        //初始化数据
        LinearLayoutManager layoutManager = new LinearLayoutManager(ApplyThirdActivity.this);//添加布局管理器
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//设置为横向水平滚动，默认是垂直
        mGridPopup.setLayoutManager(layoutManager);//设置布局管理器
        adapter = new MyContactAdapter(data, this, userid, token, url);
        mGridPopup.setAdapter(adapter);
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
     * 初始页加载
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
     * webview监听
     *
     * @param ead_web
     */
    private void wvClientSetting(BridgeWebView ead_web) {
        MyWebViewClient myWebViewClient = new MyWebViewClient(ead_web);
        ead_web.setWebViewClient(myWebViewClient);
        myWebViewClient.setOnCityClickListener(new MyWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {
                Log.e(TAG, "onCityClick: " + name);
                try {
                    if (name.contains("/api-oa/oauth")) {  //偶然几率报错  用try
                        mFabMore.setVisibility(View.GONE);
                    } else {
                        mFabMore.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    mFabMore.setVisibility(View.VISIBLE);
                }
            }
        });
        mWebChromeClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError, mLoadingPage);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mLlPopup.setVisibility(View.GONE);
        returnActivityC = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String s2 = "{tradeNo:123}";
        mNewWeb.post(new Runnable() {
            @Override
            public void run() {
                mNewWeb.evaluateJavascript("window.sdk.noticeOfPayment(\"" + s2 + "\")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.e(TAG, "onReceiveValue" + s2);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }
}
