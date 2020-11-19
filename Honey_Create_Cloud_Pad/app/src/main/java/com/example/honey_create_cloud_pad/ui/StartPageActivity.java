package com.example.honey_create_cloud_pad.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.honey_create_cloud_pad.Constant;
import com.example.honey_create_cloud_pad.MyApplication;
import com.example.honey_create_cloud_pad.R;
import com.example.honey_create_cloud_pad.bean.Result;
import com.example.honey_create_cloud_pad.http.CallBackUtil;
import com.example.honey_create_cloud_pad.http.OkhttpUtil;
import com.example.honey_create_cloud_pad.util.NetworkUtils;
import com.example.honey_create_cloud_pad.util.QMUITouchableSpan;
import com.example.honey_create_cloud_pad.util.SPUtils;
import com.example.honey_create_cloud_pad.util.VersionUtils;
import com.google.gson.Gson;
import com.xj.library.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class StartPageActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private boolean isFirstUse;//是否是第一次使用
    private AgreementDialog agreementDialog;
    @BindView(R.id.web_error)
    RelativeLayout webError;
    @BindView(R.id.tv_fresh)
    TextView tvFresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        ButterKnife.bind(this);
        boolean flag = checkNet();

        //全屏
        initScreen();

        initClick();

        if (!flag) {
            handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showAlterpPolicy();
                    }
            }, 500);

//            MyApplication.Install(this);//初始化推送

            //获取h5版本号
            getH5Version();
        }
    }

    //全屏显示
    private void initScreen() {
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    private void initClick(){
        tvFresh.setOnClickListener(v->{
            if (NetworkUtils.isConnected()) {
                startHome();
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
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    webError.setVisibility(View.VISIBLE);
                }
            }, 500);
            flag = true;
        }else {
            if (webError.getVisibility() == View.VISIBLE) {
                webError.setVisibility(View.GONE);
            }
            flag =false;
        }
        return flag;
    }

    private void showAlterpPolicy() {
        final SharedPreferences preferences = getSharedPreferences("isFirstUse", MODE_PRIVATE);
        //默认设置为true
        isFirstUse = preferences.getBoolean("isFirstUse", true);
        Log.i("StartPageActivity", "isFinishing: "+this.isFinishing() + " agreementDialog:"+agreementDialog);
        if (isFirstUse == true) {
            if (!this.isFinishing() && agreementDialog!=null) {
                agreementDialog.show();
                return;
            }
            agreementDialog = new AgreementDialog(this, generateSp("亲爱的用户，欢迎您信任并使用蜂巢制造云！\n" +
                    "您在使用蜂巢制造云产品或服务前，请认真阅读并充分理解相关用户条款、平台规则及隐私政策。当您点击同意相关条款" +
                    "，并开始使用产品或服务，即表示您已经理解并同意该条款，该条款将构成对您具有法律约束力的文件。" +
                    "用户隐私政策主要包含以下内容：个人信息及设备权限（手机号、用户名、邮箱、设备属性信息、设备位置信息、设备连接信息等）" +
                    "的收集、使用与调用等。您可以通过阅读完整版的《用户协议》和《隐私政策》了解详细信息。如您同意，" +
                    "请点击“同意并继续”开始接受我们的服务"), null, "用户协议").setBtName("同意", "不同意")
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            switch (v.getId()) {
                                case R.id.tv_dialog_ok:
                                    //实例化Editor对象
                                    SharedPreferences.Editor editor = preferences.edit();
                                    //存入数据
                                    editor.putBoolean("isFirstUse", false);
                                    //提交修改
                                    editor.commit();
                                    //这里是一开始的申请权限，不懂可以看我之前的博客
                                    startHome();
                                    break;
                                case R.id.tv_dialog_no:
                                    finish();
                                    break;
                            }
                        }
                    });
            Log.i("StartPageActivity", "showAlterpPolicy: ");
            agreementDialog.show();
        } else {
            startHome();
        }
    }

    private void startHome() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartPageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }



    private SpannableString generateSp(String text) {
        //定义需要操作的内容
        String high_light_1 = "《用户协议》";
        String high_light_2 = "《隐私政策》";

        SpannableString spannableString = new SpannableString(text);
        //初始位置
        int start = 0;
        //结束位置
        int end;
        int index;
        //indexOf(String str, int fromIndex): 返回从 fromIndex 位置开始查找指定字符在字符串中第一次出现处的索引，如果此字符串中没有这样的字符，则返回 -1。
        //简单来说，(index = text.indexOf(high_light_1, start)) > -1这部分代码就是为了查找你的内容里面有没有high_light_1这个值的内容，并确定它的起始位置
        while ((index = text.indexOf(high_light_1, start)) > -1) {
            //结束的位置
            end = index + high_light_1.length();
            spannableString.setSpan(new QMUITouchableSpan(this.getResources().getColor(R.color.blue), this.getResources().getColor(R.color.blue),
                    this.getResources().getColor(R.color.white), this.getResources().getColor(R.color.white)) {
                @Override
                public void onSpanClick(View widget) {
                    Intent intent = new Intent(StartPageActivity.this, ReminderActivity.class);
                    intent.putExtra("type", "1");
                    startActivity(intent);
                }
            }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end;
        }

        start = 0;
        while ((index = text.indexOf(high_light_2, start)) > -1) {
            end = index + high_light_2.length();
            spannableString.setSpan(new QMUITouchableSpan(this.getResources().getColor(R.color.blue), this.getResources().getColor(R.color.blue),
                    this.getResources().getColor(R.color.white), this.getResources().getColor(R.color.white)) {
                @Override
                public void onSpanClick(View widget) {
                    // 点击隐私政策的相关操作，可以使用WebView来加载一个网页
                    Intent intent = new Intent(StartPageActivity.this, ReminderActivity.class);
                    intent.putExtra("type", "2");
                    startActivity(intent);
                }
            }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end;
        }
        //最后返回SpannableString
        return spannableString;
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
     * 获取h5版本
     */
    private void getH5Version() {
        if (NetworkUtils.isConnected()) {
            Map<String, String> paramsMap =  new HashMap<>();
            paramsMap.put("equipmentId", Constant.equipmentId);
            paramsMap.put("versionCode", ""+ VersionUtils.getVersion(this));
            OkhttpUtil.okHttpGet(Constant.GET_H5_VERSION, paramsMap, new CallBackUtil.CallBackString() {
                @Override
                public void onFailure(Call call, Exception e) {
                    Log.e("StartPageActivity", "onFailure: "+e.getMessage());
                }

                @Override
                public void onResponse(String response) {
                    Log.e("StartPageActivity_TAG", "onResponse: " + response);
                    Result result = new Gson().fromJson(response, Result.class);
                    SPUtils.getInstance().put("context_url", result.getData().toString());
                }
            });
        }else {
            Toast.makeText(this,"请检查网络", Toast.LENGTH_SHORT);
            return;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (agreementDialog !=null) {
            agreementDialog.dismiss();
        }
        finish();
    }
}
