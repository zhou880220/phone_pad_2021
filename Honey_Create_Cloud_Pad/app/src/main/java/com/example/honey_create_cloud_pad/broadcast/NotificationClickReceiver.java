package com.example.honey_create_cloud_pad.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.honey_create_cloud_pad.ui.MainActivity;


/**
 * Created by wangpan on 2020/7/14
 *
 * 用于通知状态栏页面切换
 */
public class NotificationClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TAG", "userClick:我被点击啦！！！ ");
        Intent intent1 = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("APP_NOTICE_LIST","消息");
        context.startActivity(intent1);
    }
}
