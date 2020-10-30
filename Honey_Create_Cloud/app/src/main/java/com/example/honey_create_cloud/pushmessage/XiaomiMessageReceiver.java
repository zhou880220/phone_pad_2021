package com.example.honey_create_cloud.pushmessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.honey_create_cloud.MyApplication;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.ui.MainActivity;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 XiaomiMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="com.xiaomi.mipushdemo.XiaomiMessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、XiaomiMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、XiaomiMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、XiaomiMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、XiaomiMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、XiaomiMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 *
 * @author mayixiang
 */
public class XiaomiMessageReceiver extends PushMessageReceiver {

    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;
    private String mUserAccount;

    /**
     * 小米消息透传
     * @param context
     * @param message
     */
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
//        Log.v(DemoApplication.TAG,
//                "onReceivePassThroughMessage is called. " + message.toString());
        String log = context.getString(R.string.recv_passthrough_message, message.getContent());
        MainActivity.logList.add(0, getSimpleDate() + " " + log);
        Log.e("小米推送透传1", "onReceivePassThroughMessage: "+log);
        if (!TextUtils.isEmpty(message.getTopic())) { //群推透传
            mTopic = message.getTopic();
            Log.e("小米推送透传2", "onReceivePassThroughMessage: "+mTopic );
            Log.e("小米推送透传2", "onReceivePassThroughMessage: "+message.getContent() ); //获取群推透传信息
        } else if (!TextUtils.isEmpty(message.getAlias())) { //单推透传
            mAlias = message.getAlias();
            Log.e("小米推送透传3", "onReceivePassThroughMessage: "+mAlias );
            Log.e("小米推送透传3", "onReceivePassThroughMessage: "+message.getContent() ); //获取个推透传信息
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
            Log.e("小米推送透传4", "onReceivePassThroughMessage: "+mUserAccount );
        }

        Message msg = Message.obtain();
        msg.obj = log;
        MyApplication.getHandler().sendMessage(msg);
    }

    /**
     * 小米用户点击通知传递消息
     * @param context
     * @param message
     */
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.v(MyApplication.TAG,
                "onNotificationMessageClicked is called. " + message.toString());
        String log = context.getString(R.string.click_notification_message, message.getContent());
        MainActivity.logList.add(0, getSimpleDate() + " " + log);
//        Map map = JSONObject.parseObject(message.getContent(), Map.class);
//        String appid = (String) map.get("appid");

            if (!TextUtils.isEmpty(message.getTopic())) {
                mTopic = message.getTopic();
                Log.e("小米推送传递消息1", "onNotificationMessageClicked: " + mTopic);  //群推
                Log.e("小米推送传递消息1", "onNotificationMessageClicked: " + message.getContent());  //获取群推信息
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("APP_NOTICE_LIST", "消息");
                intent.putExtra("pushContentMessage", message.getContent());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            } else if (!TextUtils.isEmpty(message.getAlias())) {
                mAlias = message.getAlias();
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("APP_NOTICE_LIST", "消息");
                intent.putExtra("pushContentMessage", message.getContent());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                Log.e("小米推送传递消息2", "onNotificationMessageClicked: " + mAlias);//个推
                Log.e("小米推送传递消息2", "onNotificationMessageClicked: " + message.getContent());//获取个推消息
            } else if (!TextUtils.isEmpty(message.getUserAccount())) {
                mUserAccount = message.getUserAccount();
//            Intent intent = new Intent(context,MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            context.startActivity(intent);
                Log.e("小米推送传递消息3", "onNotificationMessageClicked: " + mUserAccount);
            }


        Message msg = Message.obtain();
        if (message.isNotified()) {
            msg.obj = log;
        }

        MyApplication.getHandler().sendMessage(msg);
    }


    /**
     * 小米消息到达获取的信息 以及在前台但不提示（在MIUI上，如果没有收到onNotificationMessageArrived回调，
     * 是因为使用的MIUI版本还不支持该特性，需要升级到MIUI7之后。非MIUI手机都可以收到这个回调）
     * @param context
     * @param message
     */
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.v(MyApplication.TAG,
                "onNotificationMessageArrived is called. " + message.toString());
        String log = context.getString(R.string.arrive_notification_message, message.getContent());
        MainActivity.logList.add(0, getSimpleDate() + " " + log);

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }

        Message msg = Message.obtain();
        msg.obj = log;
        MyApplication.getHandler().sendMessage(msg);
    }

    /**
     * 小米注册回调
     * @param context
     * @param message
     */
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.v(MyApplication.TAG,
                "onCommandResult is called. " + message.toString()+"---"+message.getCommandArguments());
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.v(MyApplication.TAG,
                "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                log = context.getString(R.string.register_success);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else {
            log = message.getReason();
        }

        Message msg = Message.obtain();
        msg.obj = log;
        MyApplication.getHandler().sendMessage(msg);
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

}
