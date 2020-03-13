package com.example.honey_create_cloud.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.file.CleanDataUtils;
import com.example.honey_create_cloud.view.AnimationView;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.newwebprogressbar)
    ProgressBar mNewwebprogressbar;
    @BindView(R.id.new_Web)
    WebView mNewWeb;
    @BindView(R.id.loading_page)
    View mLoadingPage;
    @BindView(R.id.web_error)
    View mWebError;

    //权限
    private static final int NOT_NOTICE = 2;//如果勾选了不再询问
    private AlertDialog alertDialog;
    private AlertDialog mDialog;
    private MWebViewClient mWebViewClient;
    private boolean mBackKeyPressed = false;
    private long mTime;
    private String mVersionName = "版本V1.0.0";
    private String clearSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_main);
        myRequetPermission();
        ButterKnife.bind(this);
        iniVersionName();
        webView();
        initData();
    }

    private void iniVersionName() {
        try {
            //获取包管理器
            PackageManager packageManager = getPackageManager();
            //显示安装包信息
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //获取版本号
            String versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        mWebViewClient.setOnCityClickListener(new MWebViewClient.OnCityChangeListener() {
            @Override
            public void onCityClick(String name) {
                Log.i("againurl--", name);
                if (name.equals(Constant.Login_Url)) {
                    setAndroidNativeLightStatusBar(MainActivity.this, false); //白色字体
                } else if (name.equals(Constant.Home_Url)) {
                    setAndroidNativeLightStatusBar(MainActivity.this, false);//黑色字体
                } else if (name.equals(Constant.About_Url)) {
                    setAndroidNativeLightStatusBar(MainActivity.this, true);
                } else if (name.equals(Constant.Apply_Url)) {
                    setAndroidNativeLightStatusBar(MainActivity.this, true);
                }
            }
        });
    }

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

    private void webView() {
        if (Build.VERSION.SDK_INT >= 19) {
            mNewWeb.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mNewWeb.getSettings().setLoadsImagesAutomatically(false);
        }
        WebSettings webSettings = mNewWeb.getSettings();
        WebViewSetting.initweb(webSettings);
        mNewWeb.loadUrl(Constant.Login_Url);
        mNewWeb.addJavascriptInterface(new MJavaScriptInterface(getApplicationContext()), "ApplyFunc");
        mNewWeb.loadUrl("javascript:getVersionName(\"" + mVersionName + "\")");
        mNewWeb.loadUrl("javascript:getVersionName(\"" + mVersionName + "\")");
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
    }

    private void wvClientSetting(WebView mNewWeb) {
        mWebViewClient = new MWebViewClient(mNewWeb, this, mWebError);
        mNewWeb.setWebViewClient(mWebViewClient);
        MWebChromeClient mWebChromeClient = new MWebChromeClient(this, mNewwebprogressbar);
        mNewWeb.setWebChromeClient(mWebChromeClient);
    }

    //修改顶部状态栏字体颜色
    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        } else {
            mLodingTime();
            Toast.makeText(this, "您已经申请了权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    Toast.makeText(this, "" + "权限申请成功", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOT_NOTICE) {
            myRequetPermission();//由于不知道是否选择了允许所以需要再次判断
        }
    }

    class MJavaScriptInterface {
        private Context context;

        public MJavaScriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void showApplyParams(String interfaceUrl, String appId, String token) {
            Toast.makeText(context, "调用js的Toast" + interfaceUrl, Toast.LENGTH_SHORT).show();
            Log.i("调用js的Toast", interfaceUrl);
            Intent intent = new Intent(MainActivity.this, ApplyActivity.class);
            intent.putExtra("url", interfaceUrl);
            startActivity(intent);
        }

        @JavascriptInterface
        public void NewNotifiction() {
            changeSwitch();
        }

        @JavascriptInterface
        public void ClearCache() {
            //清除缓存
            CleanDataUtils.clearAllCache(Objects.requireNonNull(MainActivity.this));
            clearSize = CleanDataUtils.getTotalCacheSize(Objects.requireNonNull(MainActivity.this));
            Toast.makeText(MainActivity.this, "缓存已清除" + clearSize, Toast.LENGTH_SHORT).show();
        }
    }

    private void changeSwitch() {
        if (!isNotificationEnabled(this)) {
            gotoSet();
        } else {
            gotoSet();
            //当前app允许消息通知
        }
    }

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


//    @Override
//    public void onBackPressed() {
//        Intent intent= new Intent(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//可在服务中用
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Toast.makeText(this, "退出程序", Toast.LENGTH_SHORT).show();
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            return true;
//        }
//        return true;
//    }
}
