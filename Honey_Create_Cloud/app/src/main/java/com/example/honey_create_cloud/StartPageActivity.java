package com.example.honey_create_cloud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.honey_create_cloud.bean.Result;
import com.example.honey_create_cloud.http.CallBackUtil;
import com.example.honey_create_cloud.http.OkhttpUtil;
import com.example.honey_create_cloud.ui.AgreementDialog;
import com.example.honey_create_cloud.ui.MainActivity;
import com.example.honey_create_cloud.ui.ReminderActivity;
import com.example.honey_create_cloud.util.QMUITouchableSpan;
import com.example.honey_create_cloud.util.SPUtils;
import com.example.honey_create_cloud.util.ScreenAdapterUtil;
import com.example.honey_create_cloud.util.VersionUtils;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

public class StartPageActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private boolean isFirstUse;//是否是第一次使用

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        MyApplication.Install(this);//初始化推送
        showAlterpPolicy();
        getInstallInfo();
        getH5Version();
    }

    private void showAlterpPolicy() {
        SharedPreferences preferences = getSharedPreferences("isFirstUse", MODE_PRIVATE);
        //默认设置为true
        isFirstUse = preferences.getBoolean("isFirstUse", true);
        if (isFirstUse == true) {
            new AgreementDialog(this, generateSp("亲爱的用户，欢迎您信任并使用蜂巢制造云！\n" +
                    "您在使用蜂巢制造云产品或服务前，请认真阅读并充分理解相关用户条款、平台规则及隐私政策。当您点击同意相关条款" +
                    "，并开始使用产品或服务，即表示您已经理解并同意该条款，该条款将构成对您具有法律约束力的文件。" +
                    "用户隐私政策主要包含以下内容：个人信息及设备权限（手机号、用户名、邮箱、设备属性信息、设备位置信息、设备连接信息等）" +
                    "的收集、使用与调用等。您可以通过阅读完整版的《用户协议》和《隐私政策》了解详细信息。如您同意，" +
                    "请点击“同意并继续”开始接受我们的服务"), null, "用户协议")
                    .setBtName("同意", "暂不使用")
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
                    }).show();
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
     * 统计安装次数
     */
    private void getInstallInfo () {
        String hasInstall = (String)SPUtils.getInstance().get(Constant.HAS_INSTALL, "0");
        int phoneType = MyApplication.getPhoneType();//(Integer) SPUtils.getInstance().get(Constant.PHONE_TYPE, Constant.PHONE_TYPE_OTHER);
        Log.e("StartPageActivity", "sendInstallInfoToServer: "+hasInstall + " phoneType:"+phoneType);
        if (hasInstall.equals("0")){//调用服务器统计数据接口
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendInstallInfoToServer(phoneType);
                }
            }, 500);

        }
    }

    /**
     * 获取h5版本信息
     */
    private void getH5Version () {

        OkhttpUtil.okHttpGet(Constant.config_url, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) { }

            @Override
            public void onResponse(String response) {
                Log.e("StartPageActivity_TAG", "onResponse2: " + response);
                Map<String,Object> map = new Gson().fromJson(response, HashMap.class);
                if (map !=null ){
                    String oldVersion = (String) SPUtils.getInstance().get(Constant.H5_VERSION, "1");
                    Log.e("StartPageActivity_TAG", "H5_VERSION: " + map.get("H5_VERSION"));
                    if (map.get("H5_VERSION")!=null && !map.get("H5_VERSION").equals(oldVersion)) {
                        SPUtils.getInstance().put(Constant.HAS_UDATE, "1");
                        SPUtils.getInstance().put(Constant.H5_VERSION, map.get("H5_VERSION"));
                    }else {
                        SPUtils.getInstance().put(Constant.HAS_UDATE, "0");
                    }
                }
            }
        });
    }


    private void sendInstallInfoToServer(int phoneType) {
        Map<String, String> paramsMap =  new HashMap<>();
        paramsMap.put("equipmentId", 3+"");
        paramsMap.put("accessEquipment", phoneType+"");
        paramsMap.put("version",  "v"+VersionUtils.getVersionName(this));
        paramsMap.put("systemName", "android "+VersionUtils.getSystemVersion());
        paramsMap.put("operationMacAddress", getId());
        String jsonStr = new Gson().toJson(paramsMap);
        Log.e("StartPageActivity", "jsonStr: "+jsonStr);
        OkhttpUtil.okHttpPostJson(Constant.APP_INSTALL_TIMES, jsonStr, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Log.e("StartPageActivity", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                Log.e("StartPageActivity_TAG", "onResponse: " + response);
                Result result = new Gson().fromJson(response, Result.class);
                if (result.getCode() == 200) {
                    SPUtils.getInstance().put(Constant.HAS_INSTALL, "1");
                } else {
                    Log.e("StartPageActivity", "服务器系统异常");
                }
            }
        });
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
        finish();
    }
}
