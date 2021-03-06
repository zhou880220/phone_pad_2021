package com.example.honey_create_cloud.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.ShareSdkBean;
import com.example.honey_create_cloud.util.ShareSDK_Web;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MyWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yzq.zxinglibrary.encode.CodeCreator;
//import com.yzq.zxinglibrary.encode.CodeCreator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsActivity extends AppCompatActivity {
    @InjectView(R.id.NewWebProgressbar)
    ProgressBar mNewWebProgressbar;
    @InjectView(R.id.new_Web_1)
    BridgeWebView mNewWeb;
    @InjectView(R.id.web_error)
    View mWebError;
    @InjectView(R.id.glide_gif)
    View mLoadingPage;
    @InjectView(R.id.new_title)
    RelativeLayout mNewTitle;
    @InjectView(R.id.new_back_image1)
    ImageView mNewBackImage1;
    @InjectView(R.id.new_title_text1)
    TextView mNewTitleText1;
    private MWebChromeClient mWebChromeClient;
    private IWXAPI wxApi;
    public static Tencent mTencent;
    private ShareSdkBean shareSdkBean;
    private Bitmap bitmap1;
    private String TAG = "NewsActivity";
    private String goBackUrl = "";

    private Handler handler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String from = intent.getStringExtra("from");
        if (from != null) {
            initView(from);
        }
        webView(url);
        mLodingTime();
    }

    private void initView(String from) {
        //?????????????????????????????????title??????
        if (from.equals("banner") || from.equals("home")) {  //?????????????????????
            mNewTitle.setVisibility(View.GONE);
        } else if (from.equals("service")) {    //????????????????????? ???????????????
            mNewTitle.setVisibility(View.VISIBLE);
        } else if (from.equals("news")) {    //????????????????????????????????? ????????????
            mNewTitle.setVisibility(View.VISIBLE);
            mNewTitleText1.setText("???????????????");
        }

        mNewBackImage1.setOnClickListener(new View.OnClickListener() {  //????????????  ??????????????????????????????
            @Override
            public void onClick(View v) {
                if (mNewWeb != null && mNewWeb.canGoBack()){
                    if (goBackUrl.contains("/mobileInformation")) { //????????????????????????
                        finish();
                    }else {
                        mNewWeb.goBack();
                    }
                }else{
                    finish();
                }
            }
        });
    }

    /**
     * webview?????????
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
        String userAgentString = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgentString + "; application-center");
        if (webSettings != null) {
            WebViewSetting.initweb(webSettings);
        }
        mNewWeb.loadUrl(url);
        //js??????????????????
        mNewWeb.addJavascriptInterface(new MyJavaScriptInterface(getApplicationContext()), "ApplyFunc");
        mNewWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mNewWeb != null && mNewWeb.canGoBack()) {
                        if (goBackUrl.contains("/mobileInformation")) { //????????????????????????
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

        //????????????????????????
        mNewWeb.registerHandler("getUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    SharedPreferences sb = getSharedPreferences("userInfoSafe", MODE_PRIVATE);
                    String userInfo = sb.getString("userInfo", "");
                    if (!userInfo.isEmpty()) {
                        function.onCallBack(userInfo);
                    } else {
                        function.onCallBack("false");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //?????????????????????????????????
        mNewWeb.registerHandler("goLogin", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
                    SharedPreferences.Editor edit1 = sp1.edit();
                    edit1.putString("apply_url", Constant.login_url);
                    edit1.commit();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //????????????
        mNewWeb.registerHandler("backNewParams", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                finish();
            }
        });

        /**
         * ?????????????????????type???????????????????????????
         */
        mNewWeb.registerHandler("shareInterface", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (!data.isEmpty()) {
                        Log.e(TAG, "shareInterface: " + data);
                        //???????????????
                        wxApi = WXAPIFactory.createWXAPI(NewsActivity.this, Constant.APP_ID);
                        wxApi.registerApp(Constant.APP_ID);
                        //QQ?????????
                        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, NewsActivity.this);

                        Map map = JSONObject.parseObject(data, Map.class);
                        String num = (String) map.get("obj");
                        Map mapType = JSONObject.parseObject(num, Map.class);
                        int type = (int) mapType.get("type");
                        String value = String.valueOf(mapType.get("data"));
                        Gson gson = new Gson();
                        ShareSdkBean shareSdkBean = gson.fromJson(value, ShareSdkBean.class);
                        if (type == 1) {
                            boolean wxAppInstalled = isWxAppInstalled(NewsActivity.this);
                            if (wxAppInstalled == true) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        wechatShare(0, shareSdkBean); //??????
                                    }
                                }).start();
                            } else {
                                Toast.makeText(NewsActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                            }
                        } else if (type == 2) {
                            boolean wxAppInstalled1 = isWxAppInstalled(NewsActivity.this);
                            if (wxAppInstalled1 == true) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        wechatShare(1, shareSdkBean); //?????????
                                    }
                                }).start();
                            } else {
                                Toast.makeText(NewsActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                            }
                        } else if (type == 3) {
                            boolean qqClientAvailable = isQQClientAvailable(NewsActivity.this);
                            if (qqClientAvailable == true) {
                                qqFriend(shareSdkBean);
                            } else {
                                Toast.makeText(NewsActivity.this, "???????????????QQ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
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
     * JS??????
     */
    class MyJavaScriptInterface implements View.OnClickListener {
        private Context context;
        private ShareSDK_Web shareSDK_web;
        private PopupWindow popupWindow;
        private PopupWindow popupWindow1;

        public MyJavaScriptInterface(Context context) {
            this.context = context;
        }


        //????????????
        @JavascriptInterface
        public void backNewParams(String flag) {
            finish();
        }

        @JavascriptInterface
        public void goLogin() {
            SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
            SharedPreferences.Editor edit1 = sp1.edit();
            edit1.putString("apply_url", Constant.login_url);
            edit1.commit();
            finish();
        }

        //????????????
        @JavascriptInterface
        public void shareSDKData(String shareData) {
            wxApi = WXAPIFactory.createWXAPI(NewsActivity.this, Constant.APP_ID);
            wxApi.registerApp(Constant.APP_ID);
            //QQ?????????
            mTencent = Tencent.createInstance(Constant.QQ_APP_ID, NewsActivity.this);
            Gson gson = new Gson();
            shareSdkBean = gson.fromJson(shareData, new ShareSdkBean().getClass());
//            getImage(shareSdkBean.getIcon());
            //???????????????
            shareSDK_web = new ShareSDK_Web(NewsActivity.this, shareData);
            View centerView = LayoutInflater.from(NewsActivity.this).inflate(R.layout.popupwindow, null);
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
                    View centerView = LayoutInflater.from(NewsActivity.this).inflate(R.layout.qrcode, null);
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
                                    saveImageToGallery(NewsActivity.this, qrCode);
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
                case R.id.wechat:
                    boolean wxAppInstalled = isWxAppInstalled(NewsActivity.this);
                    if (wxAppInstalled == true) {
                        wechatShare(0, shareSdkBean); //??????
                        popupWindow.dismiss();
                    } else {
                        Toast.makeText(context, "?????????????????????", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.wechatmoments:
                    boolean wxAppInstalled1 = isWxAppInstalled(NewsActivity.this);
                    if (wxAppInstalled1 == true) {
                        wechatShare(1, shareSdkBean); //?????????
                        popupWindow.dismiss();
                    } else {
                        Toast.makeText(context, "?????????????????????", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.qq:
                    boolean qqClientAvailable = isQQClientAvailable(NewsActivity.this);
                    if (qqClientAvailable == true) {
                        qqFriend(shareSdkBean);
                    } else {
                        Toast.makeText(context, "???????????????QQ", Toast.LENGTH_SHORT).show();
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
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQQ(NewsActivity.this, params, qqShareListener);
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
                goBackUrl = name;
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                Log.e(TAG, "onCityClick: " + name);
            }
        });
        mWebChromeClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError, mLoadingPage);
        ead_web.setWebChromeClient(mWebChromeClient);
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
            thumb = BitmapFactory.decodeStream(new URL(shareSdkBean.getIcon()).openStream());
            Log.e(TAG, "wechatShare: " + shareSdkBean.getIcon());
//??????????????????????????????120???150????????????
//???????????????????????????????????????
            Bitmap thumbBmp = compressImage(thumb);
//Bitmap??????
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

        Log.e(TAG, "compressImage: " );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 10, baos);//???????????????????????????100????????????????????????????????????????????????baos???
        int options = 100;
        while (baos.toByteArray().length / 1024 > 32) {  //?????????????????????????????????????????????32kb,??????????????????
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
     * ?????????????????????
     *
     * @param context
     * @param bmp
     */
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // ?????????????????? ???????????????
        File appDir = new File(Environment.getExternalStorageDirectory(), "zhizaoyun");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //??????????????????
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

        // ????????????????????????????????????
        String path = file.getAbsolutePath();
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // ????????????????????????
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
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
}
