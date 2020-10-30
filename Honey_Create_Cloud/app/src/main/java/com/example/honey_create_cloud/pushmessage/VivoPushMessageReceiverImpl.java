package com.example.honey_create_cloud.pushmessage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.honey_create_cloud.ui.MainActivity;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

/**
 * Created by wangpan on 2020/10/16
 * Vivo推送集成
 */
public class VivoPushMessageReceiverImpl extends OpenClientPushMessageReceiver {

    private String title = "";
    private String content = "";

    private String tragetContent = "";

    /***
     * 当通知被点击时回调此方法
     * @param context 应用上下文
     * @param upsNotificationMessage 通知详情，详细信息见API接入文档
     */
    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage upsNotificationMessage) {
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.putExtra("APP_NOTICE_LIST","消息");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(intent);
        title = upsNotificationMessage.getTitle();
        content = upsNotificationMessage.getContent();
        tragetContent = upsNotificationMessage.getTragetContent();
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("APP_NOTICE_LIST","消息");
        intent.putExtra("pushContentMessage",upsNotificationMessage.getSkipContent());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        Log.e(TAG, "Vivo推送title: " + title);
        Log.e(TAG, "Vivo推送content: " + content);
        Log.e(TAG, "Vivo推送tragetContent: " + tragetContent);
        Log.e(TAG, "Vivo推送tragetContent: " + upsNotificationMessage.getSkipContent());
    }

    /***
     * 当首次turnOnPush成功或regId发生改变时，回调此方法
     * 如需获取regId，请使用PushClient.getInstance(context).getRegId()
     * @param context 应用上下文
     * @param regId 注册id
     */
    @Override
    public void onReceiveRegId(Context context, String regId) {
        Log.e(TAG, "onReceiveRegId: " + regId);
    }
}
