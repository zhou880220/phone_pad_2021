package com.example.honey_create_cloud;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.example.honey_create_cloud.ui.MainActivity;
import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.huawei.hms.push.HmsMessaging;
import com.tencent.tinker.entry.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

/**
 * Created by wangpan on 2020/5/27
 */
public class MyApplication extends Application {
    //小米推送集成
    // user your appid the key.
    private static final String APP_ID = "2882303761518457138";
    // user your appid the key.
    private static final String APP_KEY = "5641845788138";

    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
    // com.xiaomi.mipushdemo
    public static final String TAG = "com.xiaomi.mipushdemo";

    private static DemoHandler sHandler = null;
    private static MainActivity sMainActivity = null;

    private static Context ApplicationContext;
    //热修复
    private ApplicationLike tinkerPatchApplicationLike;

    @Override
    public void onCreate() {
        super.onCreate();
        //热修复
        initTinker();

        XiaomiPush();//小米推送
        VivoPush();//Vivo推送
        OppoPush();//Oppo推送
        HuaweiPush(); // 华为推送 在添加的agconnect-services.json中
    }

    private void HuaweiPush() {
        if (android.os.Build.BRAND.toLowerCase().contains("huawei")) {
            Log.e(TAG, "AppPush: 检测到该手机是huawei定制商");
            HmsMessaging.getInstance(this);
        }else{
            Log.e(TAG, "AppPush: 检测到该手机不是huawei定制商");
        }
    }

    private void OppoPush() {
        if (android.os.Build.BRAND.toLowerCase().contains("oppo")) {
            Log.e(TAG, "AppPush: 检测到该手机是oppo定制商");
            initOppoPush();
        } else {
            Log.e(TAG, "AppPush: 检测到该手机不是oppo定制商");
        }
    }

    /**
     * 初始化OPPO推送
     */
    protected void initOppoPush() {
        //在执行Oppo推送注册之前，需要先判断当前平台是否支持Oppo推送
//        boolean supportPush = HeytapPushManager.isSupportPush();
//        Log.e(TAG, "initOppoPush: "+supportPush);
//        if (HeytapPushManager.isSupportPush()) {
        HeytapPushManager.init(getApplicationContext(), true);
        HeytapPushManager.register(this, "7d57ed01f7984b3dbc6e01584261d1e1", "02b23105de92454f8a1145989d0b7589", new ICallBackResultService() {
            @Override
            public void onRegister(int code, String s) {
                if (code == 0) {
                    SharedPreferences huaWeiSharedPreferences = getSharedPreferences("OppoPushToken",MODE_PRIVATE);
                    SharedPreferences.Editor edit = huaWeiSharedPreferences.edit();
                    edit.putString("OppoToken",s);
                    edit.commit();
                    Log.e("注册成功Oppo", "registerId:" + s);
                } else {
                    Log.e("注册失败", "code=" + code + ",msg=" + s);
                }
            }

            @Override
            public void onUnRegister(int code) {
                if (code == 0) {
                    Log.e("注销成功", "code=" + code);
                } else {
                    Log.e("注销失败", "code=" + code);
                }
            }

            @Override
            public void onSetPushTime(int code, String s) {
                Log.e("SetPushTime", "code=" + code + ",result:" + s);
            }

            @Override
            public void onGetPushStatus(int code, int status) {
                if (code == 0 && status == 0) {
                    Log.e("Push状态正常", "code=" + code + ",status=" + status);
                } else {
                    Log.e("Push状态错误", "code=" + code + ",status=" + status);
                }
            }

            @Override
            public void onGetNotificationStatus(int code, int status) {
                if (code == 0 && status == 0) {
                    Log.e("通知状态正常", "code=" + code + ",status=" + status);
                } else {
                    Log.e("通知状态错误", "code=" + code + ",status=" + status);
                }
            }
        });//setPushCallback接口也可设置callback
        HeytapPushManager.requestNotificationPermission();
    }
//    }

    private void VivoPush() {
        if (Build.BRAND.toLowerCase().contains("vivo")) {
            //vivo推送集成初始化
            PushClient.getInstance(getApplicationContext()).initialize();
            PushClient.getInstance(getApplicationContext()).turnOnPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int i) {
                    // TODO: 开关状态处理， 0代表成功
                    Log.e(TAG, "" + i);
                }
            });
        } else {
            Log.e(TAG, "AppPush: 检测到该手机不是vivo定制商");
        }

    }

    private void XiaomiPush() {
        if (android.os.Build.BRAND.toLowerCase().contains("xiaomi")) {
            Log.e(TAG, "AppPush: 检测到该手机为小米定制商");
            if (shouldInit()) {
                MiPushClient.registerPush(this, APP_ID, APP_KEY);
            }

            LoggerInterface newLogger = new LoggerInterface() {

                @Override
                public void setTag(String tag) {
                    // ignore
                }

                @Override
                public void log(String content, Throwable t) {
                    Log.d(TAG, content, t);
                }

                @Override
                public void log(String content) {
                    Log.d(TAG, content);
                }
            };
            Logger.setLogger(this, newLogger);
            if (sHandler == null) {
                sHandler = new DemoHandler(getApplicationContext());
            }
        } else {
            Log.e(TAG, "AppPush: 检测到该手机不是小米定制商");
        }
        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
    }

    private void initTinker() {
        // 我们可以从这里获得Tinker加载过程的信息
        tinkerPatchApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
        // 初始化TinkerPatch SDK
        TinkerPatch.init(tinkerPatchApplicationLike)
                .reflectPatchLibrary()
                .setPatchRollbackOnScreenOff(true)
                .setFetchPatchIntervalByHours(3)
                .setPatchRestartOnSrceenOff(true);
        TinkerPatch.with().fetchPatchUpdate(true);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static DemoHandler getHandler() {
        return sHandler;
    }

    public static void setMainActivity(MainActivity activity) {
        sMainActivity = activity;
    }

    public static void Install(Context context) {
        ApplicationContext = context;
    }

    public static class DemoHandler extends Handler {

        private Context context;

        public DemoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            String s = (String) msg.obj;
            if (sMainActivity != null) {
//                sMainActivity.refreshLogInfo();
            }
            if (!TextUtils.isEmpty(s)) {
                Log.e(TAG, s);
            }
        }
    }
}
