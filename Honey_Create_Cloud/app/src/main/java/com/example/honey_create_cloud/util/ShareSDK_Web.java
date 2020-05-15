package com.example.honey_create_cloud.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.ShareSdkBean;
import com.google.gson.Gson;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by wangpan on 2020/4/27
 */
public class ShareSDK_Web {
    private Context context;
    private String content;
    private ShareSdkBean shareSdkBean;

    public ShareSDK_Web(Context context, String content) {
        this.context = context;
        this.content = content;
        init();
    }

    private void init() {
        Gson gson = new Gson();
        shareSdkBean = gson.fromJson(content, new ShareSdkBean().getClass());

    }

    public void QQshowShare() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(shareSdkBean.getTitle());
        sp.setTitleUrl(shareSdkBean.getUrl()); // 标题的超链接
        sp.setText(shareSdkBean.getTxt());
        sp.setImageUrl(shareSdkBean.getIcon());
        Platform qzone = ShareSDK.getPlatform(QQ.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qzone.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
            }

            public void onComplete(Platform arg0, int arg1, HashMap arg2) {
                //分享成功的回调
                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
            }

            public void onCancel(Platform arg0, int arg1) {
                //取消分享的回调
                Toast.makeText(context, "用户取消分享", Toast.LENGTH_SHORT).show();
            }
        });
        // 执行图文分享
        qzone.share(sp);
    }

    public void QZoneshowShare() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(shareSdkBean.getTitle());
        sp.setTitleUrl(shareSdkBean.getUrl()); // 标题的超链接
        sp.setText(shareSdkBean.getTxt());
        sp.setImageUrl(shareSdkBean.getIcon());

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qzone.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
            }

            public void onComplete(Platform arg0, int arg1, HashMap arg2) {
                //分享成功的回调
                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
            }

            public void onCancel(Platform arg0, int arg1) {
                //取消分享的回调
                Toast.makeText(context, "用户取消分享", Toast.LENGTH_SHORT).show();
            }
        });
        // 执行图文分享
        qzone.share(sp);
    }

    public void SinaweiboshowShare() {

//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("url", shareSdkBean.getIcon());
//            jsonObject.put("width", 120);
//            jsonObject.put("height", 120);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
//        Platform.ShareParams sp = new Platform.ShareParams();
//        sp.setText(shareSdkBean.getTxt());
//        sp.setLcDisplayName(shareSdkBean.getUrl());
//        sp.setLcImage(jsonObject);
//        sp.setLcSummary(shareSdkBean.getTitle());
//        sp.setLcUrl(shareSdkBean.getUrl());
//        sp.setLcObjectType("webpage");
//        platform.setPlatformActionListener(new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancel(Platform platform, int i) {
//                Toast.makeText(context, "用户取消分享", Toast.LENGTH_SHORT).show();
//            }
//        });
//        platform.share(sp);

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle("测试分享的标题");
        sp.setText("测试分享的文本 http://m.zhizaoyun.com/app/news/news.html");
        sp.setImagePath("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1587638830408&di=72286457faf0bf14766b5ecc1f7bb31c&imgtype=0&src=http%3A%2F%2Fbiaoqingba.cn%2Fwp-content%2Fuploads%2F2018%2F03%2Fcdf7475427f1807-3.jpeg");
        Platform qzone = ShareSDK.getPlatform (SinaWeibo.NAME);
// 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qzone.setPlatformActionListener (new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
            }
            public void onComplete(Platform arg0, int arg1, HashMap arg2) {
                //分享成功的回调
                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
            }
            public void onCancel(Platform arg0, int arg1) {
                //取消分享的回调
                Toast.makeText(context, "用户取消分享", Toast.LENGTH_SHORT).show();
            }
        });
// 执行图文分享
        qzone.share(sp);
    }

    public void WechatMomentsshowShare() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle(shareSdkBean.getTitle());
        sp.setTitleUrl(shareSdkBean.getUrl()); // 标题的超链接
        sp.setText(shareSdkBean.getTxt());
//        Bitmap logo = BitmapFactory.decodeResource(context.getResources(),R.drawable.wechat);
//        sp.setImageData(logo);
        sp.setImagePath("/sdcard/FindYou/lottie/img_9.png");
//        sp.setImageUrl(shareSdkBean.getIcon());  /sdcard/FindYou/lottie/img_9.png

        Platform qzone = ShareSDK.getPlatform(WechatMoments.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qzone.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
            }

            public void onComplete(Platform arg0, int arg1, HashMap arg2) {
                //分享成功的回调
                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
            }

            public void onCancel(Platform arg0, int arg1) {
                //取消分享的回调
                Toast.makeText(context, "用户取消分享", Toast.LENGTH_SHORT).show();
            }
        });
        // 执行图文分享
        qzone.share(sp);
    }

    public void WechatshowShare() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle(shareSdkBean.getTitle());
        sp.setTitleUrl(shareSdkBean.getUrl()); // 标题的超链接
        sp.setText(shareSdkBean.getTxt());
//        Bitmap logo = BitmapFactory.decodeResource(context.getResources(),R.drawable.wechat);
//        sp.setImageData(logo);
//        sp.setImageUrl(shareSdkBean.getIcon());
        sp.setImagePath("/sdcard/FindYou/lottie/img_9.png");

        Platform qzone = ShareSDK.getPlatform(Wechat.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qzone.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
            }

            public void onComplete(Platform arg0, int arg1, HashMap arg2) {
                //分享成功的回调
                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
            }

            public void onCancel(Platform arg0, int arg1) {
                //取消分享的回调
                Toast.makeText(context, "用户取消分享", Toast.LENGTH_SHORT).show();
            }
        });
        // 执行图文分享
        qzone.share(sp);

    }
}
