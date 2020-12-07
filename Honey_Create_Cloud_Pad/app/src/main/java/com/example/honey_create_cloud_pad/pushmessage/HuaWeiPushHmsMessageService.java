package com.example.honey_create_cloud_pad.pushmessage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.honey_create_cloud_pad.bean.HwNotification;
import com.google.gson.Gson;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.huawei.hms.push.SendException;

import java.util.Arrays;


public class HuaWeiPushHmsMessageService extends HmsMessageService {
    private String TAG = "HAUWEI";
    private final static String CODELABS_ACTION = "com.example.honey_create_cloud_pad";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i(TAG, "received refresh token:" + token);
        // send the token to your app server.
        if (!TextUtils.isEmpty(token)) {
            // This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
            refreshedTokenToServer(token);
        }
//        Intent intent = new Intent();
//        intent.setAction(CODELABS_ACTION);
//        intent.putExtra("method", "onNewToken");
//        intent.putExtra("msg", "onNewToken called, token: " + token);

    }

    /**
     * accessEquipment 接入设备 (1：iphone；2：huawei；3：mi；4：oppo；5：vivo)
     * equipmentType 设备类型 (1：pc；2：android_pad；3：android_phone；4：ios_pad；5：ios_phone)
     */

    private void refreshedTokenToServer(String token) {
        Log.e(TAG, "sending token to server. token:" + token);
        SharedPreferences huaWeiSharedPreferences = getSharedPreferences("HuaWeiPushToken",MODE_PRIVATE);
        SharedPreferences.Editor edit = huaWeiSharedPreferences.edit();
        edit.putString("huaWeiToken",token);
        edit.commit();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "onMessageReceived token:" + remoteMessage.getToken());
        if (remoteMessage == null) {
            Log.e(TAG, "Received remoteMessage entity is null!");
            return;
        }
        Log.e(TAG, "getCollapseKey: " + remoteMessage.getCollapseKey()
                + "\n getData: " + remoteMessage.getData()
                + "\n getFrom: " + remoteMessage.getFrom()
                + "\n getTo: " + remoteMessage.getTo()
                + "\n getDataOfMap: " + remoteMessage.getDataOfMap().get("Badge")
                + "\n getMessageId: " + remoteMessage.getMessageId()
                + "\n getOriginalUrgency: " + remoteMessage.getOriginalUrgency()
                + "\n getUrgency: " + remoteMessage.getUrgency()
                + "\n getSendTime: " + remoteMessage.getSentTime()
                + "\n getMessageType: " + remoteMessage.getMessageType()
                + "\n getTtl: " + remoteMessage.getTtl());

        try {
            HwNotification hw = new Gson().fromJson(remoteMessage.getData(), HwNotification.class);
            Log.e(TAG, "hw getBadgeNumber: "+ hw.getBadge());
            String badge = remoteMessage.getDataOfMap().get("badge");
            int i = hw.getBadge();//Integer.parseInt(hw.getBadge());
            Log.e(TAG, "getBadgeNumber: "+badge );
            Bundle extra = new Bundle();
            extra.putString("package", "com.example.honey_create_cloud_pad");
            extra.putString("class", "com.example.honey_create_cloud_pad.StartPageActivity");
            extra.putInt("badgenumber", i);
            getApplicationContext().getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            Log.e("huawei", "\n getImageUrl: " + notification.getImageUrl()
                    + "\n getTitle: " + notification.getTitle()
                    + "\n getTitle: " + notification.getBadgeNumber()
                    + "\n getTitleLocalizationKey: " + notification.getTitleLocalizationKey()
                    + "\n getTitleLocalizationArgs: " + Arrays.toString(notification.getTitleLocalizationArgs())
                    + "\n getBody: " + notification.getBody()
                    + "\n getBodyLocalizationKey: " + notification.getBodyLocalizationKey()
                    + "\n getBodyLocalizationArgs: " + Arrays.toString(notification.getBodyLocalizationArgs())
                    + "\n getIcon: " + notification.getIcon()
                    + "\n getSound: " + notification.getSound()
                    + "\n getTag: " + notification.getTag()
                    + "\n getColor: " + notification.getColor()
                    + "\n getClickAction: " + notification.getClickAction()
                    + "\n getChannelId: " + notification.getChannelId()
                    + "\n getLink: " + notification.getLink()
                    + "\n getNotifyId: " + notification.getNotifyId());
        }
    }


    @Override
    public void onTokenError(Exception e) {
        super.onTokenError(e);
    }

}
