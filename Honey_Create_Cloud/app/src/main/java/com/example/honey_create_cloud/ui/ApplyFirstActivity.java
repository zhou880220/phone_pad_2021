package com.example.honey_create_cloud.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
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

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.honey_create_cloud.BuildConfig;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.adapter.MyContactAdapter;
import com.example.honey_create_cloud.bean.BrowserBean;
import com.example.honey_create_cloud.bean.PictureUpload;
import com.example.honey_create_cloud.bean.RecentlyApps;
import com.example.honey_create_cloud.bean.ShareSdkBean;
import com.example.honey_create_cloud.bean.TakePhoneBean;
import com.example.honey_create_cloud.bean.TitleName;
import com.example.honey_create_cloud.recorder.AudioRecorderButton;
import com.example.honey_create_cloud.util.FileUtil;
import com.example.honey_create_cloud.util.ShareSDK_Web;
import com.example.honey_create_cloud.util.SystemUtil;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MyWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.encode.CodeCreator;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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

import static com.example.honey_create_cloud.ui.ClipImageActivity.REQ_CLIP_AVATAR;
import static com.example.honey_create_cloud.ui.MainActivity.getRealPathFromUri;

public class ApplyFirstActivity extends AppCompatActivity {
    @InjectView(R.id.NewWebProgressbar)
    ProgressBar mNewWebProgressbar;
    @InjectView(R.id.new_Web1)
    BridgeWebView mNewWeb;
    @InjectView(R.id.web_error)
    View mWebError;
    @InjectView(R.id.glide_gif)
    View mLoadingPage;
    @InjectView(R.id.reload_tv)
    TextView mReloadTv;
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
    @InjectView(R.id.dimiss_popup)
    RelativeLayout mDimissPopup;
    @InjectView(R.id.apply_back_image1)
    ImageView mApplyBackImage1;
    @InjectView(R.id.apply_title_text1)
    TextView mApplyTitleText1;
    @InjectView(R.id.apply_menu_image1)
    ImageView mApplyMenuImage1;
    @InjectView(R.id.apply_menu_home1)
    ImageView mApplyMenuHome1;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case OPLOAD_IMAGE: {
                    Log.e(TAG, "handleMessage: " + msg.obj);
                    String newName = (String) msg.obj;
                    OkHttpClient client1 = new OkHttpClient();
                    final FormBody formBody = new FormBody.Builder()
                            .add("fileNames", newName)
                            .add("bucketName", Constant.prod_bucket_Name)
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
                            String fileUrl = data.get(0).getFileUrl();
                            String imageUrl = newName + "&&" + fileUrl;
                            mNewWeb.post(new Runnable() {
                                @Override
                                public void run() {
                                    mNewWeb.evaluateJavascript("window.sdk.AlreadyPhoto(\"" + imageUrl + "\")", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {

                                        }
                                    });
                                    mNewWeb.callHandler("AlreadyPhoto", imageUrl, new CallBackFunction() {
                                        @Override
                                        public void onCallBack(String data) {
                                        }
                                    });
                                }
                            });
                        }
                    });
                    break;
                }
                case TITLENAME: {
                    String titlename = (String) msg.obj;
                    if (titlename != null) {
                        mApplyTitleText1.setText(titlename);
                    }
                    break;
                }
            }
            return false;
        }
    });

    private static final String TAG = "ApplyFirstActivity_TAG";
    private MyContactAdapter adapter;
    private boolean isShow;
    private String token;
    private String url;
    private String userid;
    private List<RecentlyApps.DataBean> data;
    private MWebChromeClient mWebChromeClient;
    public static boolean returnActivityA;
    private String appId;
    //二维码 返回码
    private static final int REQUEST_CODE_SCAN = 1;
    //请求相机 返回码
    private static final int REQUEST_CAPTURE = 100;
    //请求相册 返回码
    private static final int REQUEST_PICK = 101;
    //获取文件路径 返回码
    private static final int REQUEST_CODE = 6;
    //修改头像回调handler
    private static final int OPLOAD_IMAGE = 2;
    //标题名称
    private static final int TITLENAME = 3;
    //调用照相机返回图片文件
    private File tempFile;
    private String accessToken;
    //所有的联系人信息
    private Cursor personCur;
    //所有联系人电话号码
    private Cursor numberCur;
    //拼接联系人名称及电话号
    private StringBuffer stringBuffer;
    private String goBackUrl;
    private IWXAPI wxApi;
    public static Tencent mTencent;
    private ShareSdkBean shareSdkBean;
    //    private Bitmap bitmap1;
    private HashMap<String, String> hashMap = new HashMap<String, String>();
    private RecentlyApps recentlyApps;
    private RecyclerView mGridPopup;
    private String appUrlData;
    private Uri imageUriThreeApply;
    private String applyTitleName;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_first);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        token = intent.getStringExtra("token");
        userid = intent.getStringExtra("userid");
        appId = intent.getStringExtra("appId");
        intentOkhttp();
        intentAppNameOkhttp();
        initviewTitle();
        webView(url);

        mLodingTime();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refreshPay");
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }

    private void initviewTitle() {

        mApplyBackImage1.setOnClickListener(new View.OnClickListener() {  //返回
            @Override
            public void onClick(View v) {
                if (mNewWeb != null && mNewWeb.canGoBack()) {
                    if (goBackUrl.contains("systemIndex")) { //电子看板
                        finish();
                    } else if (goBackUrl.contains("mobileHome/")) { //制造云头条
                        finish();
                    } else if (goBackUrl.contains("index.html")) {  //图纸通
                        finish();
                    } else if (goBackUrl.contains("yyzx_dianji/")) { //电机功率
                        finish();
                    } else if (mWebError.getVisibility() == View.VISIBLE) {
                        finish();
                    } else {
                        mNewWeb.goBack();
                    }
                } else {
                    finish();
                }
            }
        });

        mApplyMenuImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundAlpha(ApplyFirstActivity.this, 0.5f);//0.0-welcome1.0
                View centerView = LayoutInflater.from(ApplyFirstActivity.this).inflate(R.layout.windowpopup, null);
                PopupWindow popupWindow = new PopupWindow(centerView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(false);
                popupWindow.setAnimationStyle(R.style.pop_animation);
                popupWindow.showAtLocation(centerView, Gravity.BOTTOM, 0, 0);
                mGridPopup = centerView.findViewById(R.id.grid_popup);
                RelativeLayout mRelativeLayout = centerView.findViewById(R.id.go_apply_home);
                Button mDismissPopupButton = centerView.findViewById(R.id.dismiss_popup_button);
                pagerView();
                adapter.setOnClosePopupListener(new MyContactAdapter.OnClosePopupListener() {
                    @Override
                    public void onClosePopupClick(String name) {
                        if (name.equals("关闭") && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                });
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        backgroundAlpha(ApplyFirstActivity.this, 1f);
                    }
                });
                mDismissPopupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backgroundAlpha(ApplyFirstActivity.this, 1f);
                        popupWindow.dismiss();
                    }
                });
                mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sp = ApplyFirstActivity.this.getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("apply_url", Constant.apply_url);
                        edit.commit();
                        finish();
                    }
                });
            }
        });

        mApplyMenuHome1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp1 = ApplyFirstActivity.this.getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                SharedPreferences.Editor edit1 = sp1.edit();
                edit1.putString("apply_url", Constant.text_url);
                edit1.commit();
                finish();
            }
        });
    }

    /**
     * webview初始化
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
        //js交互接口定义
        mNewWeb.addJavascriptInterface(new MJavaScriptInterface(getApplicationContext()), "ApplyFunc");
        mNewWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mNewWeb != null && mNewWeb.canGoBack()) {
                        if (goBackUrl.contains("systemIndex")) { //电子看板
                            finish();
                        } else if (goBackUrl.contains("mobileHome/")) { //制造云头条
                            finish();
                        } else if (goBackUrl.contains("index.html")) {  //图纸通
                            finish();
                        } else if (goBackUrl.contains("yyzx_dianji/")) { //电机功率
                            finish();
                        } else if (mWebError.getVisibility() == View.VISIBLE) {
                            finish();
                        } else {
                            mNewWeb.goBack();
                        }
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
                function.onCallBack("{" + "\"" + "version" + "\"" + ":\"" + "Android" + SystemUtil.getSystemVersion() + "\"" + ",\"" + "model" + "\"" + ":\"" + SystemUtil.getSystemModel() + "\"" + "}");
            }
        });
        /**
         * 获取手机唯一标识符
         */
        mNewWeb.registerHandler("getIdentifier", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    String imei = SystemUtil.getUniqueIdentificationCode(ApplyFirstActivity.this);
                    function.onCallBack(imei);
                } catch (Exception e) {
                    String id = getId();
                    function.onCallBack(id);
                }
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
                    Toast.makeText(ApplyFirstActivity.this, "获取用户数据异常", Toast.LENGTH_SHORT).show();
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
                    gotoPhoto();
                }
            }
        });
        /**
         * 获取通讯录
         */
        mNewWeb.registerHandler("getMailList", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                stringBuffer = new StringBuffer();
                String allContancts = getAllContancts(stringBuffer);
                String substring = allContancts.substring(0, allContancts.length() - 1);//把最后边拼接的逗号去掉
                function.onCallBack(substring + "]");
            }
        });
        /**
         * 录制语音
         */
        mNewWeb.registerHandler("openVoice", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                View centerView = LayoutInflater.from(ApplyFirstActivity.this).inflate(R.layout.recorder_layout, null);
                PopupWindow popupWindow = new PopupWindow(centerView, ViewGroup.LayoutParams.MATCH_PARENT, 290);
                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(false);
                popupWindow.showAtLocation(centerView, Gravity.BOTTOM, 0, 0);

                AudioRecorderButton mAudioRecorderButton = centerView.findViewById(R.id.id_recorder_button);
                mAudioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
                    @Override
                    public void onFinish(float seconds, String filePath) {
                        String s = tobase64(filePath);
                        function.onCallBack("{" + "\"" + "success" + "\"" + ":\"" + "true" + "\"" + ",\"" + "data" + "\"" + ":\"" + s + "\"" + "}");
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                });
            }
        });
        /**
         * 上传文件
         */
        mNewWeb.registerHandler("upLoadFile", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
            }
        });
        /**
         * @param key 用于用户读取临时数据
         */
        mNewWeb.registerHandler("getCookie", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (data != null) {
                    Map map = JSONObject.parseObject(data, Map.class);
                    Set<String> set = map.keySet();
                    Iterator<String> iterator = set.iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = (String) map.get(key);
                        String getCookieValue = (String) hashMap.get(value);
                        function.onCallBack(getCookieValue);
                    }
                }
            }
        });
        /**
         * 下载文件
         */
        mNewWeb.registerHandler("downLoadFile", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Toast.makeText(ApplyFirstActivity.this, "请稍后...", Toast.LENGTH_SHORT).show();
                Map map = JSONObject.parseObject(data, Map.class);
                String num = (String) map.get("url");
                String filename = (String) map.get("filename");
                if (filename != null && !filename.equals("")) {
                    String newReplaceUrl = num.replace(num.substring(num.lastIndexOf("/") + 1), filename);
                    Log.e(TAG, "新的文件名下载路径: 2" + newReplaceUrl);
                    List<RecentlyApps.DataBean> Listdata = recentlyApps.getData();
                    for (int i = 0; i < Listdata.size() - 1; i++) {
                        String ApplyId = String.valueOf(Listdata.get(i).getAppId());
                        if (appId.equals(ApplyId)) {
                            char[] chars = Listdata.get(i).getAppName().toCharArray();
                            String pinYinHeadChar = getPinYinHeadChar(chars);
                            String FileLoad = "zhizaoyun/download/" + pinYinHeadChar + "/";
                            downFilePath(FileLoad, newReplaceUrl);
                        }
                    }
                } else {
                    Log.e(TAG, "新的文件名下载路径:3 " + num);
                    List<RecentlyApps.DataBean> Listdata = recentlyApps.getData();
                    for (int i = 0; i < Listdata.size() - 1; i++) {
                        String ApplyId = String.valueOf(Listdata.get(i).getAppId());
                        if (appId.equals(ApplyId)) {
                            char[] chars = Listdata.get(i).getAppName().toCharArray();
                            String pinYinHeadChar = getPinYinHeadChar(chars);
                            String FileLoad = "zhizaoyun/download/" + pinYinHeadChar + "/";
                            downFilePath(FileLoad, num);
                        }
                    }
                }
            }
        });

        /**
         * 一下注释掉的功能延期开放
         */

        /**
         * 用户取消授权
         */
        mNewWeb.registerHandler("cancelAuthorization", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                finish();
            }
        });
        /**
         * 分享更具传递的type类型进行分享的页面
         */
        mNewWeb.registerHandler("shareInterface", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e(TAG, "shareInterface: " + data);
                //微信初始化
                wxApi = WXAPIFactory.createWXAPI(ApplyFirstActivity.this, Constant.APP_ID);
                wxApi.registerApp(Constant.APP_ID);
                //QQ初始化
                mTencent = Tencent.createInstance(Constant.QQ_APP_ID, ApplyFirstActivity.this);

                Map map = JSONObject.parseObject(data, Map.class);
                String num = (String) map.get("obj");
                Map mapType = JSONObject.parseObject(num, Map.class);
                int type = (int) mapType.get("type");
                String value = String.valueOf(mapType.get("data"));
                Gson gson = new Gson();
                ShareSdkBean shareSdkBean = gson.fromJson(value, ShareSdkBean.class);
                if (type == 1) {
                    boolean wxAppInstalled = isWxAppInstalled(ApplyFirstActivity.this);
                    if (wxAppInstalled == true) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                wechatShare(0, shareSdkBean); //好友
                            }
                        }).start();
                    } else {
                        Toast.makeText(ApplyFirstActivity.this, "手机未安装微信", Toast.LENGTH_SHORT).show();
                    }
                } else if (type == 2) {
                    boolean wxAppInstalled1 = isWxAppInstalled(ApplyFirstActivity.this);
                    if (wxAppInstalled1 == true) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                wechatShare(1, shareSdkBean); //朋友圈
                            }
                        }).start();
                    } else {
                        Toast.makeText(ApplyFirstActivity.this, "手机未安装微信", Toast.LENGTH_SHORT).show();
                    }
                } else if (type == 3) {
                    boolean qqClientAvailable = isQQClientAvailable(ApplyFirstActivity.this);
                    if (qqClientAvailable == true) {
                        qqFriend(shareSdkBean);
                    } else {
                        Toast.makeText(ApplyFirstActivity.this, "手机未安装QQ", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
        });
        /**
         * 用户登录异常回跳登录页
         */
        mNewWeb.registerHandler("goLogin", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                SharedPreferences.Editor edit1 = sp1.edit();
                edit1.putString("apply_url", Constant.login_url);
                edit1.commit();
                finish();
            }
        });
        /**
         * 用户登录异常回跳首页
         */
        mNewWeb.registerHandler("backHome", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                SharedPreferences.Editor edit1 = sp1.edit();
                edit1.putString("apply_url", Constant.text_url);
                edit1.commit();
                finish();
            }
        });
        /**
         * 用户打开系统浏览器
         */
        mNewWeb.registerHandler("intentBrowser", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
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
        });
        /**
         * 拨打电话
         */
        mNewWeb.registerHandler("openCall", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e(TAG, "openCall: 1" + data);
                Map map = JSONObject.parseObject(data, Map.class);
                String num = (String) map.get("num");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        /**
         * 跳转支付页面，传递商品信息
         */
        mNewWeb.registerHandler("purchaseOfEntry", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Map map = JSONObject.parseObject(data, Map.class);
                String num = (String) map.get("obj");
                if (!num.isEmpty()) {
                    Intent intent = new Intent(ApplyFirstActivity.this, IntentOpenActivity.class);
                    intent.putExtra("PurchaseOfEntry", num);
                    intent.putExtra("appId", appId);
                    intent.putExtra("token", token);
                    startActivity(intent);
                    Log.e(TAG, "商品信息1: " + num);
                }
            }
        });
        /**
         * 存储用户信息
         */
        mNewWeb.registerHandler("setCookie", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Map map = JSONObject.parseObject(data, Map.class);
                String num = (String) map.get("str");
                String cookieKey = "key";
                String cookieValue = "value";
                ArrayList<Object> list = new ArrayList<>();
                List objects = JSONObject.parseObject(num, List.class);
                if (objects != null && objects.size() > 0) {
                    for (Object o : objects) {
                        if (o != null) {
                            Map JsonMap = JSONObject.parseObject(o.toString(), Map.class);
                            String key = (String) JsonMap.get(cookieKey);
                            String value = (String) JsonMap.get(cookieValue);
                            hashMap.put(key, value);
                        }
                    }
                }
                Log.e(TAG, "setCookie: " + num);
            }
        });
        /**
         * 打开扫一扫功能
         */
        mNewWeb.registerHandler("startIntentZing", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Intent intent = new Intent(ApplyFirstActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        });
        /**
         * 拨打电话
         */
        mNewWeb.registerHandler("OpenPayIntent", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e(TAG, "打开通讯录: " + data);
                Map map = JSONObject.parseObject(data, Map.class);
                String tele = (String) map.get("tele");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tele));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //打开手机系统通知界面
        mNewWeb.registerHandler("openNotification", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                gotoSet();
            }
        });
    }


    class MJavaScriptInterface implements View.OnClickListener {
        private Context context;
        private ShareSDK_Web shareSDK_web;
        private PopupWindow popupWindow1;
        private PopupWindow popupWindow;
        private ProgressDialog progressDialog;


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
                Log.e(TAG, "purchaseOfEntry: " + purchaseOfEntry);
                Intent intent = new Intent(ApplyFirstActivity.this, IntentOpenActivity.class);
                intent.putExtra("PurchaseOfEntry", purchaseOfEntry);
                intent.putExtra("appId", appId);
                intent.putExtra("token", token);
//              returnActivityA = true;
                startActivity(intent);
            }
        }

        //打开系统通知界面 无Bridge
        @JavascriptInterface
        public void openNotification() {
            gotoSet();
        }

        //用户取消权限
        @JavascriptInterface
        public void cancelAuthorization() {
//            returnActivityA = false;
            finish();
        }

        //存储本地数据 无Bridge
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
            Intent intent = new Intent(ApplyFirstActivity.this, CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SCAN);
        }

        //启动本地浏览器
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

        //登录异常回到首页
        @JavascriptInterface
        public void backHome() {
            SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
            SharedPreferences.Editor edit1 = sp1.edit();
            edit1.putString("apply_url", Constant.text_url);
            edit1.commit();
            finish();
        }

        //登陆异常到登录页
        @JavascriptInterface
        public void goLogin() {
            SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
            SharedPreferences.Editor edit1 = sp1.edit();
            edit1.putString("apply_url", Constant.login_url);
            edit1.commit();
            finish();
        }

        //下载文件保存到PartLib/download/
        @JavascriptInterface
        public void downLoadFile(String downPath) {
            Toast.makeText(context, "请稍后...", Toast.LENGTH_SHORT).show();
            List<RecentlyApps.DataBean> data = recentlyApps.getData();
            for (int i = 0; i < data.size() - 1; i++) {
                String ApplyId = String.valueOf(data.get(i).getAppId());
                if (appId.equals(ApplyId)) {
                    char[] chars = data.get(i).getAppName().toCharArray();
                    String pinYinHeadChar = getPinYinHeadChar(chars);
                    String FileLoad = "zhizaoyun/download/" + pinYinHeadChar + "/";
                    downFilePath(FileLoad, downPath);
                }
            }
//
//            if (newFileName != null || !newFileName.equals("")){
//                String newReplaceUrl = downPath.replace(downPath.substring(downPath.lastIndexOf("/") + 1), newFileName);
//                List<RecentlyApps.DataBean> Listdata = recentlyApps.getData();
//                for (int i = 0; i < Listdata.size() - 1; i++) {
//                    String ApplyId = String.valueOf(Listdata.get(i).getAppId());
//                    if (appId.equals(ApplyId)) {
//                        char[] chars = Listdata.get(i).getAppName().toCharArray();
//                        String pinYinHeadChar = getPinYinHeadChar(chars);
//                        String FileLoad = "zhizaoyun/download/" + pinYinHeadChar + "/";
//                        downFilePath(FileLoad, newReplaceUrl);
//                    }
//                }
//            }else{
//                List<RecentlyApps.DataBean> Listdata = recentlyApps.getData();
//                for (int i = 0; i < Listdata.size() - 1; i++) {
//                    String ApplyId = String.valueOf(Listdata.get(i).getAppId());
//                    if (appId.equals(ApplyId)) {
//                        char[] chars = Listdata.get(i).getAppName().toCharArray();
//                        String pinYinHeadChar = getPinYinHeadChar(chars);
//                        String FileLoad = "zhizaoyun/download/" + pinYinHeadChar + "/";
//                        downFilePath(FileLoad, downPath);
//                    }
//                }
//            }
        }

        /**
         * @param cookiemessage 用于存储用户临时数据
         */
        @JavascriptInterface
        public void setCookie(String cookiemessage) {
            String cookieKey = "key";
            String cookieValue = "value";
            ArrayList<Object> list = new ArrayList<>();
            List objects = JSONObject.parseObject(cookiemessage, List.class);
            if (objects != null && objects.size() > 0) {
                for (Object o : objects) {
                    if (o != null) {
                        Map map = JSONObject.parseObject(o.toString(), Map.class);
                        String key = (String) map.get(cookieKey);
                        String value = (String) map.get(cookieValue);
                        hashMap.put(key, value);
                    }
                }
            }
        }


        //分享功能
        @JavascriptInterface
        public void shareSDKData(String shareData) {
            //微信初始化
            wxApi = WXAPIFactory.createWXAPI(ApplyFirstActivity.this, Constant.APP_ID);
            wxApi.registerApp(Constant.APP_ID);
            //QQ初始化
            mTencent = Tencent.createInstance(Constant.QQ_APP_ID, ApplyFirstActivity.this);
            Gson gson = new Gson();
            shareSdkBean = gson.fromJson(shareData, new ShareSdkBean().getClass());
//            getImage(shareSdkBean.getIcon());
            //集成分享类
            shareSDK_web = new ShareSDK_Web(ApplyFirstActivity.this, shareData);
            View centerView = LayoutInflater.from(ApplyFirstActivity.this).inflate(R.layout.popupwindow, null);
            popupWindow = new PopupWindow(centerView, ViewGroup.LayoutParams.MATCH_PARENT,
                    400);
            popupWindow.setTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(false);
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
                    shareSDK_web.CopyUrl();
                    popupWindow.dismiss();
                    break;
                case R.id.Qrcode: {
                    View centerView = LayoutInflater.from(ApplyFirstActivity.this).inflate(R.layout.qrcode, null);
                    popupWindow1 = new PopupWindow(centerView, ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow1.setTouchable(true);
                    popupWindow1.setFocusable(true);
                    popupWindow1.setOutsideTouchable(false);
                    popupWindow1.showAtLocation(centerView, Gravity.CENTER, 0, 0);
                    ImageView mErWeiMaImage = centerView.findViewById(R.id.main_image);
                    try {
                        Bitmap qrCode = CodeCreator.createQRCode(shareSdkBean.getUrl(), 200, 200, null);
                        if (qrCode != null) {
                            mErWeiMaImage.setImageBitmap(qrCode);
                            mErWeiMaImage.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    saveImageToGallery(ApplyFirstActivity.this, qrCode);
                                    return false;
                                }
                            });
                            popupWindow.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case R.id.wechat: {
                    boolean wxAppInstalled = isWxAppInstalled(ApplyFirstActivity.this);
                    if (wxAppInstalled == true) {
                        wechatShare(0, shareSdkBean); //好友
                        popupWindow.dismiss();
                    } else {
                        Toast.makeText(context, "手机未安装微信", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case R.id.wechatmoments: {
                    boolean wxAppInstalled1 = isWxAppInstalled(ApplyFirstActivity.this);
                    if (wxAppInstalled1 == true) {
                        wechatShare(1, shareSdkBean); //朋友圈
                        popupWindow.dismiss();
                    } else {
                        Toast.makeText(context, "手机未安装微信", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case R.id.qq:
                    boolean qqClientAvailable = isQQClientAvailable(ApplyFirstActivity.this);
                    if (qqClientAvailable == true) {
                        qqFriend(shareSdkBean);
                    } else {
                        Toast.makeText(context, "手机未安装QQ", Toast.LENGTH_SHORT).show();
                    }
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
     * 判断是否安装了QQ
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
     * 发送给QQ朋友
     */
    int shareType = 1;
    //IMG
    public static String IMG = "";
    int mExtarFlag = 0x00;

    private void qqFriend(ShareSdkBean shareSdkBean) {
        final Bundle params = new Bundle();
        //
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareSdkBean.getTitle()); //分享的标题
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareSdkBean.getUrl());//分享的链接
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareSdkBean.getTxt());//分享的摘要

        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareSdkBean.getIcon());//分享的图片
//        params.putString(shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL
//                : QQShare.SHARE_TO_QQ_IMAGE_URL, IMG);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getPackageName());
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

        doShareToQQ(params);
        return;
    }

    private void doShareToQQ(final Bundle params) {

        // QQ分享要在主线程做
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQQ(ApplyFirstActivity.this, params, qqShareListener);
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

    private void downFilePath(String fileLoad, String downPath) {
        FileDownloader.setup(ApplyFirstActivity.this);
        FileDownloader.getImpl().create(downPath)
                .setPath(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileLoad + getNameFromUrl(downPath))
                .setForceReDownload(true)
                .setListener(new FileDownloadListener() {
                    //等待
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    //下载进度回调
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                            progressBar.setProgress((soFarBytes * 100 / totalBytes));
//                            progressDialog.setProgress((soFarBytes * 100 / totalBytes));
                    }

                    //下载完成
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        String[] split1 = task.getPath().split("0/");
                        Toast.makeText(ApplyFirstActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(ApplyFirstActivity.this)
                                .setTitle("保存路径：")
                                .setMessage(split1[1])
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }

                    //暂停
                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    //下载出错
                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Toast.makeText(ApplyFirstActivity.this, "下载异常", Toast.LENGTH_SHORT).show();
                    }

                    //已存在相同下载
                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();

    }

    public static String getPinYinHeadChar(char[] chars) {
        StringBuffer sb = new StringBuffer();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(chars[i], defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * @param flag (0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare(int flag, ShareSdkBean shareSdkBean) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareSdkBean.getUrl();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareSdkBean.getTitle();
        msg.description = shareSdkBean.getTxt();
        //这里替换一张自己工程里的图片资源
//        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.wechat);
        Bitmap thumb = null;
        try {
            thumb = BitmapFactory.decodeStream(new URL(shareSdkBean.getIcon()).openStream());
//注意下面的这句压缩，120，150是长宽。
//一定要压缩，不然会分享失败
            Bitmap thumbBmp = compressImage(thumb);
//Bitmap回收
//            bitmap1.recycle();
            msg.thumbData = bmpToByteArray(thumbBmp, true);
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
        wxApi.sendReq(req);
    }

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 32) {  //循环判断如果压缩后图片是否大于32kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 1;//每次都减少1
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
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
     * 保存图片到相册
     *
     * @param context
     * @param bmp
     */
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片 创建文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(), "zhizaoyun");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //图片文件名称
        String fileName = "shy_" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        String path = file.getAbsolutePath();
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
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

    /**
     * 获取通讯录中的联系人及号码
     */
    private String getAllContancts(StringBuffer sb) {
        sb.append("[");
        // 获取手机通讯录信息
        ContentResolver resolver = this.getContentResolver();
        // 获取联系人信息
        personCur = resolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        if (personCur == null) {
            try {//此处适配了6.0权限
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                Intent intentSet = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                startActivity(intentSet);
            }
            return null;
        }
        // 循环遍历，获取每个联系人的姓名和电话号码
        while (personCur.moveToNext()) {
            // 联系人姓名
            String cname = "";
            String clientname = "clientname";
            // 联系人电话
            String cnum = "";
            String clientnum = "clientnum";
            // 联系人id号码
            String ID;
            ID = personCur.getString(personCur.getColumnIndex(ContactsContract.Contacts._ID));
            // 联系人姓名
            cname = personCur.getString(personCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            // id的整型数据
            int id = Integer.parseInt(ID);
            if (id > 0) {
                // 获取指定id号码的电话号码
                Cursor c = resolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ID, null, null);
                // 遍历游标
                while (c.moveToNext()) {
                    cnum = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (!TextUtils.isEmpty(cname)) {
//                        list.add(new PhoneCallBean(cname, cnum));//查询通讯录中所有联系人
                        sb.append("{").append("\"" + clientname + "\"").append(":").append("\"" + cname + "\"").append(",")
                                .append("\"" + clientnum + "\"").append(":").append("\"" + cnum + "\"").append("}").append(",");
                    }
                }
                if (c != null && !c.isClosed())
                    c.close();
            }
        }
        try {
            if (personCur != null && !personCur.isClosed()) {
                personCur.close();
            }
        } catch (Exception e) {
        }
        return sb.toString();
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
//                byte[] bytes = new byte[]{};
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
     * 获取当前应用链接
     */
    private void intentAppNameOkhttp() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request builder = new Request.Builder()
                .url(Constant.GETAPPLY_URL + appId)
                .get()
                .build();
        okHttpClient.newCall(builder).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    String string = response.body().string();
                    Gson gson = new Gson();
                    TitleName titleName = gson.fromJson(string, TitleName.class);
                    Message message = new Message();
                    message.what = TITLENAME;
                    message.obj = titleName.getData();
                    handler.sendMessage(message);
                } else {

                }
            }
        });
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
                    Log.e(TAG, "onResponse: " + string);
                    Gson gson = new Gson();
                    recentlyApps = gson.fromJson(string, RecentlyApps.class);
                    data = recentlyApps.getData();
                } else {

                }
            }
        });
    }

    /**
     * 系统回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (data != null) {
                if (null == uploadMessage && null == uploadMessageAboveL) return;
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                // Uri result = (((data == null) || (resultCode != RESULT_OK)) ? null : data.getData());
                if (result == null) {
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                        uploadMessage = null;
                    } else if (uploadMessageAboveL != null) {
                        uploadMessageAboveL.onReceiveValue(null);
                        uploadMessageAboveL = null;
                    }
                }
                if (uploadMessageAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, data, result);
                } else if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(result);
                    uploadMessage = null;
                }
            } else if (imageUriThreeApply != null) {
                uploadMessageAboveL.onReceiveValue(new Uri[]{imageUriThreeApply});
            } else {
                //这里uploadMessage跟uploadMessageAboveL在不同系统版本下分别持有了
                //WebView对象，在用户取消文件选择器的情况下，需给onReceiveValue传null返回值
                //否则WebView在未收到返回值的情况下，无法进行任何操作，文件选择器会失效
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                } else if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(null);
                    uploadMessageAboveL = null;
                }
            }
        } else {
            //这里uploadMessage跟uploadMessageAboveL在不同系统版本下分别持有了
            //WebView对象，在用户取消文件选择器的情况下，需给onReceiveValue传null返回值
            //否则WebView在未收到返回值的情况下，无法进行任何操作，文件选择器会失效
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            } else if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
        }

        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
        }

        switch (requestCode) {
            case REQUEST_CODE_SCAN: //二维码扫描
            {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String stringExtra = data.getStringExtra(com.yzq.zxinglibrary.common.Constant.CODED_CONTENT);
                        mNewWeb.evaluateJavascript("window.sdk.getCodeUrl(\"" + stringExtra + "\")", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });
                        /**
                         * 一下注释掉的功能延期开放
                         */
                        mNewWeb.callHandler("getCodeUrl", stringExtra, new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {

                            }
                        });
                    }
                }
            }
            break;
            case REQUEST_CAPTURE://调用系统相机返回
            {
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                } else if (resultCode == RESULT_CANCELED) {
                    mNewWeb.post(new Runnable() {
                        @Override
                        public void run() {
                            mNewWeb.evaluateJavascript("window.sdk.AlreadyPhoto(\"" + "取消" + "\")", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                }
                            });
                            mNewWeb.callHandler("AlreadyPhoto", "取消", new CallBackFunction() {
                                @Override
                                public void onCallBack(String data) {
                                }
                            });
                        }
                    });
                }
            }
            break;
            case REQUEST_PICK://调用系统相册返回
            {
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String realPathFromUri = getRealPathFromUri(this, uri);
                    if (realPathFromUri.endsWith(".jpg") || realPathFromUri.endsWith(".png") || realPathFromUri.endsWith(".jpeg")) {
                        gotoClipActivity(uri);
                    } else {
                        mNewWeb.evaluateJavascript("window.sdk.AlreadyPhoto(\"" + "取消" + "\")", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                            }
                        });
                        mNewWeb.callHandler("AlreadyPhoto", "取消", new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {
                            }
                        });
                        Toast.makeText(this, "选择的格式不对,请重新选择", Toast.LENGTH_SHORT).show();
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    mNewWeb.post(new Runnable() {
                        @Override
                        public void run() {
                            mNewWeb.evaluateJavascript("window.sdk.AlreadyPhoto(\"" + "取消" + "\")", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                }
                            });
                            mNewWeb.callHandler("AlreadyPhoto", "取消", new CallBackFunction() {
                                @Override
                                public void onCallBack(String data) {
                                }
                            });
                        }
                    });
                }
            }
            break;

            case REQ_CLIP_AVATAR: //图片裁剪
            {
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = FileUtil.getRealFilePathFromUri(getApplicationContext(), uri);
                    Log.e(TAG, "onActivityResult: " + cropImagePath);
                    takePhoneUrl(cropImagePath);
                } else {
                    mNewWeb.evaluateJavascript("window.sdk.AlreadyPhoto(\"" + "取消" + "\")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                        }
                    });
                    mNewWeb.callHandler("AlreadyPhoto", "取消", new CallBackFunction() {
                        @Override
                        public void onCallBack(String data) {
                        }
                    });
                }
            }
            break;
            default:
                break;
        }
    }

    String path;

    // 选择内容回调到Html页面
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent, Uri uri) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        if ("file".equalsIgnoreCase(intent.getScheme())) {//使用第三方应用打开
            path = intent.getDataString();
            return;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
            path = getPath(this, uri);
        } else {//4.4以下下系统调用方法
            path = getRealPathFromURI(uri);
        }

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                    if (path == null) {
                        String nameFromUrl = getNameFromUrl(uri.toString());
                        mNewWeb.evaluateJavascript("window.sdk.getFileInfo(\"" + nameFromUrl + "\")", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });
                        /**
                         * 一下注释掉的功能延期开放
                         */
                        mNewWeb.callHandler("getFileInfo", nameFromUrl, new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {

                            }
                        });
                    } else {
                        String nameFromUrl = getNameFromUrl(path);
                        mNewWeb.evaluateJavascript("window.sdk.getFileInfo(\"" + nameFromUrl + "\")", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });
                        /**
                         * 一下注释掉的功能延期开放
                         */
                        mNewWeb.callHandler("getFileInfo", nameFromUrl, new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {

                            }
                        });
                    }
                }
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    //4.4以下下系统调用方法 将uri转文件真实路径
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @NonNull
    private String getNameFromUrl(String url) {
        Log.e(TAG, "原subUrl: " + url);
        String subUrl = url.substring(url.lastIndexOf("/") + 1);
        Log.e(TAG, "截取的subUrl: " + subUrl);
        if (!TextUtils.isEmpty(subUrl) && subUrl.contains("%")) {
            try {
                subUrl = URLDecoder.decode(subUrl, StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, "转换的subUrl: " + subUrl);
        }
        Log.e(TAG, "现subUrl: " + subUrl);
        return subUrl == null ? url.substring(url.lastIndexOf("/") + 1) : subUrl;
    }

    //上传头像
    private void takePhoneUrl(String cropImagePath) {

        accessToken = "Bearer" + " " + token;
        OkHttpClient client = new OkHttpClient();//创建okhttpClient
        //创建body类型用于传值
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = new File(cropImagePath);
        if (file == null) {
            return;
        }
        final MediaType mediaType = MediaType.parse("image/jpeg");//创建媒房类型
        builder.addFormDataPart("fileObjs", file.getName(), RequestBody.create(mediaType, file));
        builder.addFormDataPart("fileNames", "");
        builder.addFormDataPart("bucketName", Constant.prod_bucket_Name);
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

    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        ClipImageActivity.goToClipActivity(this, uri);
    }

    // 广播通知
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String paySuccessError = intent.getStringExtra("paySuccessError");
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
        int id = v.getId();
        switch (id) {
            case R.id.tv_publish:
                mTvPublish.setBackgroundResource(R.mipmap.floatinghomechange);
                mTvMyPublish.setBackgroundResource(R.mipmap.floatingapply);
                mTvRelation.setBackgroundResource(R.mipmap.floatingapp);
//                returnActivityA = false;
//                returnActivityB = false;
//                returnActivityC = false;
                SharedPreferences sp1 = this.getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                SharedPreferences.Editor edit1 = sp1.edit();
                edit1.putString("apply_url", Constant.text_url);
                edit1.commit();
                finish();
                break;
            case R.id.tv_myPublish:
                mTvPublish.setBackgroundResource(R.mipmap.floatinghome);
                mTvMyPublish.setBackgroundResource(R.mipmap.floatingapplychange);
                mTvRelation.setBackgroundResource(R.mipmap.floatingapp);
//                returnActivityA = false;
//                returnActivityB = false;
//                returnActivityC = false;
                SharedPreferences sp = this.getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("apply_url", Constant.apply_url);
                edit.commit();
                finish();
                break;
            case R.id.tv_relation: //
            {
                backgroundAlpha(this, 0.5f);//0.0-welcome1.0
                View centerView = LayoutInflater.from(ApplyFirstActivity.this).inflate(R.layout.windowpopup, null);
                PopupWindow popupWindow = new PopupWindow(centerView, ViewGroup.LayoutParams.MATCH_PARENT,
                        940);
                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(false);
                popupWindow.setAnimationStyle(R.style.pop_animation);
                popupWindow.showAtLocation(centerView, Gravity.BOTTOM, 0, 0);
                mGridPopup = centerView.findViewById(R.id.grid_popup);
                Button mDismissPopupButton = centerView.findViewById(R.id.dismiss_popup_button);
                pagerView();

                mTvPublish.setBackgroundResource(R.mipmap.floatinghome);
                mTvMyPublish.setBackgroundResource(R.mipmap.floatingapply);
                mTvRelation.setBackgroundResource(R.mipmap.floatingappchange);
                switchPopup();
                adapter.setOnClosePopupListener(new MyContactAdapter.OnClosePopupListener() {
                    @Override
                    public void onClosePopupClick(String name) {
                        if (name.equals("关闭") && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                });
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        backgroundAlpha(ApplyFirstActivity.this, 1f);
                    }
                });
                mDismissPopupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backgroundAlpha(ApplyFirstActivity.this, 1f);
                        popupWindow.dismiss();
                    }
                });
            }
            break;
            case R.id.fab_more:
                switchPopup();
                break;
            case R.id.dimiss_popup:
                mDimissPopup.setVisibility(View.GONE);
                switchPopup();
                break;
        }
    }

    /**
     * papawin设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
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
            mDimissPopup.setVisibility(View.VISIBLE);
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
            mDimissPopup.setVisibility(View.GONE);

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(ApplyFirstActivity.this);//添加布局管理器
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

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    /**
     * webview监听
     *
     * @param ead_web
     */
    private void wvClientSetting(BridgeWebView ead_web) {
        MyWebViewClient myWebViewClient = new MyWebViewClient(ead_web, mWebError);
        ead_web.setWebViewClient(myWebViewClient);
        myWebViewClient.setOnCityClickListener(new MyWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {
                goBackUrl = name;
                Log.e(TAG, "onCityClick: " + name);
                WebBackForwardList webBackForwardList = mNewWeb.copyBackForwardList();
                boolean b = webBackForwardList.getCurrentIndex() != webBackForwardList.getSize() - 1;
                try {
                    if (name.contains("/api-oa/oauth")) {  //偶然几率报错  用try
                        mApplyBackImage1.setVisibility(View.GONE);
                    } else {
                        mApplyBackImage1.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    mApplyBackImage1.setVisibility(View.VISIBLE);
                }
            }
        });
        mWebChromeClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError, mLoadingPage);
//        ead_web.setWebChromeClient(mWebChromeClient);

        ead_web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //进度条消失
                    if (mLoadingPage != null) {
                        mLoadingPage.setVisibility(View.GONE);
                        mNewWebProgressbar.setVisibility(View.GONE);
                    } else {
                        mNewWebProgressbar.setVisibility(View.GONE);
                    }
                } else {
                    //进度跳显示
                    mNewWebProgressbar.setVisibility(View.VISIBLE);
                    mNewWebProgressbar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openFileChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openFileChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openFileChooserActivity();
            }

            // For Android >= 5.0 打开系统文件管理系统
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                String[] acceptTypes = fileChooserParams.getAcceptTypes();
                uploadMessageAboveL = filePathCallback;
                if (acceptTypes[0].equals("*/*")) {
                    openFileChooserActivity(); //文件系统管理
                } else if (acceptTypes[0].equals("image/*")) {
                    openImageChooserActivity();//打开系统拍照及相册选取
                } else if (acceptTypes[0].equals("video/*")) {
                    openVideoChooserActivity();//打开系统拍摄/选取视频
                }

                return true;
            }
        });
    }

    /**
     * 跳转到用户拍摄/选取视频
     */
    public void openVideoChooserActivity() {
        backgroundAlpha(this, 0.5f);//0.0-welcome1.0
        View centerView = LayoutInflater.from(ApplyFirstActivity.this).inflate(R.layout.video_chooser_popup, null);
        PopupWindow videoPopupWindow = new PopupWindow(centerView, ViewGroup.LayoutParams.MATCH_PARENT,
                465);
        videoPopupWindow.setTouchable(true);
        videoPopupWindow.setFocusable(false);
        videoPopupWindow.setOutsideTouchable(false);
        videoPopupWindow.setAnimationStyle(R.style.pop_animation);
        videoPopupWindow.showAtLocation(centerView, Gravity.BOTTOM, 0, 0);
        mGridPopup = centerView.findViewById(R.id.grid_popup);
//        Photograph_popup
//                Photo_album_popup
        Button mPhotoGraphPopupButton = centerView.findViewById(R.id.Photo_graph_popup); //用户点击拍摄按钮
        Button mPhotoAlbumPopup = centerView.findViewById(R.id.Photo_album_popup);      //用户点击视频选取按钮
        Button mDismissPopupButton = centerView.findViewById(R.id.video_dismiss_popup_button);  //用户点击取消按钮

        mPhotoGraphPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundAlpha(ApplyFirstActivity.this, 1f);//0.0-welcome1.0
                videoPopupWindow.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                //限制时长
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                //开启摄像机
                startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
            }
        });

        mPhotoAlbumPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundAlpha(ApplyFirstActivity.this, 1f);//0.0-welcome1.0
                videoPopupWindow.dismiss();

                if (android.os.Build.BRAND.equals("Huawei")) {
                    Intent intentPic = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentPic, FILE_CHOOSER_RESULT_CODE);
                }
                if (android.os.Build.BRAND.equals("Xiaomi")) {//是否是小米设备,是的话用到弹窗选取入口的方法去选取视频
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
                    startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), FILE_CHOOSER_RESULT_CODE);
                } else {//直接跳到系统相册去选取视频
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT < 19) {
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("video/*");
                    } else {
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("video/*");
                    }
                    startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), FILE_CHOOSER_RESULT_CODE);
                }
            }
        });

        mDismissPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundAlpha(ApplyFirstActivity.this, 1f);//0.0-welcome1.0
                videoPopupWindow.dismiss();
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                } else if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(null);
                    uploadMessageAboveL = null;
                }
            }
        });


    }

    /**
     * 跳转到用户拍照/选取相册
     */
    public void openImageChooserActivity() {
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator;
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        imageUriThreeApply = Uri.fromFile(new File(filePath + fileName));
//相册相机选择窗
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriThreeApply);
        Intent Photo = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooserIntent = Intent.createChooser(Photo, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
        startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE);
    }

    /**
     * 跳转到系统文件选择
     */
    public void openFileChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");//文件上传
        startActivityForResult(i, FILE_CHOOSER_RESULT_CODE);
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
//        returnActivityA = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        returnActivityA = true;
        String s2 = "{tradeNo:123}";
        mNewWeb.post(new Runnable() {
            @Override
            public void run() {
                mNewWeb.evaluateJavascript("window.sdk.noticeOfPayment(\"" + s2 + "\")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                    }
                });
            }
        });
        /**
         * 一下注释掉的功能延期开放
         */
        mNewWeb.callHandler("noticeOfPayment", s2, new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

            }
        });
    }

    /**
     * 判断微信是否安装
     *
     * @param context
     * @return true 已安装   false 未安装
     */
    public static boolean isWxAppInstalled(Context context) {
        IWXAPI wxApi = WXAPIFactory.createWXAPI(context, null);
        wxApi.registerApp(Constant.APP_ID);
        boolean bIsWXAppInstalled = false;
        bIsWXAppInstalled = wxApi.isWXAppInstalled();
        return bIsWXAppInstalled;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }
}
