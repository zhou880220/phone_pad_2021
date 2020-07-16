package com.example.honey_create_cloud.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.ShareSdkBean;
import com.example.honey_create_cloud.util.ScreenAdapterUtil;
import com.example.honey_create_cloud.util.ShareSDK_Web;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MyWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
    private MWebChromeClient mWebChromeClient;
    private IWXAPI wxApi;
    private ShareSdkBean shareSdkBean;
    private Bitmap bitmap1;
    private String TAG = "NewsActivity";
    private String goBackUrl = "";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        boolean rects = ScreenAdapterUtil.hasNotchInScreen(this);
        if (rects == true) {
            //有刘海屏
            setAndroidNativeLightStatusBar(NewsActivity.this, false);//白色字体
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            getWindow().setAttributes(lp);
        } else if (rects == false) {
            //无刘海屏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            setAndroidNativeLightStatusBar(NewsActivity.this, true);//黑色字体
        }
        setContentView(R.layout.activity_news);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        webView(url);
        mLodingTime();
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
        String userAgentString = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgentString + "; application-center");
        if (webSettings != null) {
            WebViewSetting.initweb(webSettings);
        }
        mNewWeb.loadUrl(url);
        //js交互接口定义
        mNewWeb.addJavascriptInterface(new MyJavaScriptInterface(getApplicationContext()), "ApplyFunc");
        mNewWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mNewWeb != null && mNewWeb.canGoBack()) {
                        if (goBackUrl.contains("mobileInformation")) {
//                            returnActivityA = false;
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
                    function.onCallBack("false");
                }
            }
        });
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
     * JS交互
     */
    class MyJavaScriptInterface implements View.OnClickListener {
        private Context context;
        private ShareSDK_Web shareSDK_web;
        private PopupWindow popupWindow;
        private PopupWindow popupWindow1;

        public MyJavaScriptInterface(Context context) {
            this.context = context;
        }


        //关闭页面
        @JavascriptInterface
        public void backNewParams(String flag) {
            if (!flag.isEmpty()) {
                finish();
            } else {

            }
        }

        @JavascriptInterface
        public void goLogin() {
            SharedPreferences sp1 = getSharedPreferences("apply_urlSafe", MODE_PRIVATE);
            SharedPreferences.Editor edit1 = sp1.edit();
            edit1.putString("apply_url", Constant.login_url);
            edit1.commit();
            finish();
        }

        //分享功能
        @JavascriptInterface
        public void shareSDKData(String shareData) {
            wxApi = WXAPIFactory.createWXAPI(NewsActivity.this, Constant.APP_ID);
            wxApi.registerApp(Constant.APP_ID);
            Gson gson = new Gson();
            shareSdkBean = gson.fromJson(shareData, new ShareSdkBean().getClass());
//            getImage(shareSdkBean.getIcon());
            //集成分享类
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
                        wechatShare(0); //好友
                        popupWindow.dismiss();
                    }else{
                        Toast.makeText(context, "手机未安装微信", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.wechatmoments:
                    boolean wxAppInstalled1 = isWxAppInstalled(NewsActivity.this);
                    if (wxAppInstalled1 == true) {
                        wechatShare(1); //朋友圈
                        popupWindow.dismiss();
                    }else{
                        Toast.makeText(context, "手机未安装微信", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.qq:
//                    shareSDK_web.QQshowShare();
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
                Log.e(TAG, "onCityClick: "+name);
            }
        });
        mWebChromeClient = new MWebChromeClient(this, mNewWebProgressbar, mWebError, mLoadingPage);
        ead_web.setWebChromeClient(mWebChromeClient);
    }

//    public void getImage(String path) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                URL imageUrl = null;
//                try {
//                    imageUrl = new URL(path);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
//                    conn.setDoInput(true);
//                    conn.connect();
//                    InputStream is = conn.getInputStream();
//                    Bitmap bitmap = BitmapFactory.decodeStream(is);
//                    bitmap1 = createBitmapThumbnail(bitmap, false);
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            public Bitmap createBitmapThumbnail(Bitmap bitmap, boolean needRecycler) {
//                int width = bitmap.getWidth();
//                int height = bitmap.getHeight();
//                int newWidth = 80;
//                int newHeight = 80;
//                float scaleWidth = ((float) newWidth) / width;
//                float scaleHeight = ((float) newHeight) / height;
//                Matrix matrix = new Matrix();
//                matrix.postScale(scaleWidth, scaleHeight);
//                Bitmap newBitMap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//                if (needRecycler) bitmap.recycle();
//                return newBitMap;
//            }
//        }).start();
//    }

    /**
     * @param flag (0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare(int flag) {
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
            Log.e(TAG, "wechatShare: " + shareSdkBean.getIcon());
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
            Log.e("111", e.getMessage());
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        String path = file.getAbsolutePath();
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
        } catch (FileNotFoundException e) {
            Log.e("333", e.getMessage());
            e.printStackTrace();
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
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
}
