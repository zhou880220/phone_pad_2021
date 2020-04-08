package com.example.honey_create_cloud.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.honey_create_cloud.BuildConfig;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.MyHandlerCallBack;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.HeadPic;
import com.example.honey_create_cloud.bean.PictureUpload;
import com.example.honey_create_cloud.file.CleanDataUtils;
import com.example.honey_create_cloud.util.FileUtil;
import com.example.honey_create_cloud.util.ScreenAdapterUtil;
import com.example.honey_create_cloud.view.AnimationView;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MWebViewClient;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.example.honey_create_cloud.ui.ClipImageActivity.REQ_CLIP_AVATAR;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.newwebprogressbar)
    ProgressBar mNewwebprogressbar;
    @InjectView(R.id.new_Web)
    BridgeWebView mNewWeb;
    @InjectView(R.id.loading_page)
    View mLoadingPage;
    @InjectView(R.id.web_error)
    View mWebError;
    @InjectView(R.id.head_image3)
    ImageView headImage3;
    private Uri imageUri;
    private int REQUEST_CODE = 1234;
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;

    private static final String TAG = "MainActivity_TAG";

    //调用照相机返回图片文件
    private File tempFile;
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
        public boolean handleMessage(Message msg) {
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
                        } else {
                            Toast.makeText(MainActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
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
        ButterKnife.inject(this);
        iniVersionName();
        myRequetPermission();
        webView();
    }


    /**
     * 初始化webview js交互
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void webView() {
        if (Build.VERSION.SDK_INT >= 19) {
            mNewWeb.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mNewWeb.getSettings().setLoadsImagesAutomatically(false);
        }
        WebSettings webSettings = mNewWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true);

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        //Handler做为通信桥梁的作用，接收处理来自H5数据及回传Native数据的处理，当h5调用send()发送消息的时候，调用MyHandlerCallBack
        mNewWeb.setDefaultHandler(new MyHandlerCallBack(mOnSendDataListener));
        myChromeWebClient = new MWebChromeClient(this, mNewwebprogressbar, mWebError);
        mNewWeb.setWebChromeClient(myChromeWebClient);

//        mNewWeb.setWebViewClient(new MWebViewClientw());
        mNewWeb.loadUrl(Constant.Attention);

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
                Log.i(TAG, "replace---" + data);
                if (!data.isEmpty()){
                    String replace1 = data.replace("\"", "");
                    String replace2 = replace1.replace("token:", "");
                    String replace3 = replace2.replace("{", "");
                    String replace4 = replace3.replace("}", "");
                    String[] s = replace4.split(" ");
                    Log.i(TAG, "getTakeCamerareplace---" + replace1);
                    token1 = s[0];
                    userid = s[1];
                    Log.i(TAG, "getTakeCameratoken1---" + token1);
                    Log.i(TAG, "getTakeCamerauserid---" + userid);
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
                    Log.i(TAG, "token1---" + token1);
                    Log.i(TAG, "userid---" + userid);
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
    }

    /**
     * 跳转到照相机
     */
    private void gotoCamera() {
        Log.d("evan", "*****************打开相机********************");

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

//        Log.d(TAG, "*****************打开相机********************");
//        //创建拍照存储的图片文件
//        tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/myImage/"), System.currentTimeMillis() + ".jpg");
//
//        //跳转到调用系统相机
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
//            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            Uri contentUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", tempFile);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
//            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//            for (ResolveInfo resolveInfo : resInfoList) {
//                String packageName = resolveInfo.activityInfo.packageName;
//                grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            }
//
//        } else {
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
//        }
//        startActivityForResult(intent, REQUEST_CAPTURE);
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
                Log.e(TAG, "onResume");
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
                Log.e(TAG, "onStart");
            }
        });
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestart() {
        mNewWeb.evaluateJavascript("window.sdk.notification()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onRestart");
            }
        });
        super.onRestart();
    }

    /**
     * webview 监听
     *
     * @param mNewWeb
     */
    private void wvClientSetting(BridgeWebView mNewWeb) {
        mWebViewClient = new MWebViewClient(mNewWeb, this, mWebError);
        mNewWeb.setWebViewClient(mWebViewClient);
        MWebChromeClient mWebChromeClient = new MWebChromeClient(this, mNewwebprogressbar, mWebError);
        mNewWeb.setWebChromeClient(mWebChromeClient);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
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

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
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
                    String realPathFromUri = getRealPathFromUri(this, uri);
                    if (realPathFromUri.endsWith(".jpg") || realPathFromUri.endsWith(".png") || realPathFromUri.endsWith(".jpeg")){
                        Log.e(TAG,""+realPathFromUri);
                        gotoClipActivity(uri);
                    }else{
                        Log.e(TAG,""+realPathFromUri);
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
                    headImage3.setImageBitmap(bitMap);
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
                                Toast.makeText(MainActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
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
            if (!interfaceUrl.isEmpty()){
                Intent intent = new Intent(MainActivity.this, ApplyFirstActivity.class);
                intent.putExtra("url", interfaceUrl);
                intent.putExtra("token", usertoken1);
                intent.putExtra("userid", userid1);
                Log.i(TAG, "showApplyParamstoken1---" + usertoken1 + "____" + userid1);
                startActivity(intent);
            }else {
                Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void showNewsParams(String addressUrl, String appId, String token) {
            Log.i("调用js的Toast", addressUrl);
            if (!addressUrl.isEmpty()){
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("url", addressUrl);
                intent.putExtra("token", token1);
                startActivity(intent);
            } else{
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

