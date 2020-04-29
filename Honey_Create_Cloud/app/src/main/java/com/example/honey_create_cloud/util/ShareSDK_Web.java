package com.example.honey_create_cloud.util;

import android.content.Context;
import android.widget.Toast;

import com.example.honey_create_cloud.bean.ShareSdkBean;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
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

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("url", "http://wx4.sinaimg.cn/large/006WfoFPly1fq0jo9svnaj30dw0dwdhv.jpg");
            jsonObject.put("width", 120);
            jsonObject.put("height", 120);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setText("第一次测试");
        sp.setLcCreateAt("2019-01-24");
        sp.setLcDisplayName("displayName测试");
        sp.setLcImage(jsonObject);
        sp.setLcSummary("Summary测试");
        sp.setLcUrl("http://www.mob.com/");
        sp.setLcObjectType("webpage");
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Toast.makeText(context, "用户取消分享", Toast.LENGTH_SHORT).show();
            }
        });
        platform.share(sp);

//        Platform.ShareParams sp = new Platform.ShareParams();
//        sp.setTitle("测试分享的标题");
//        sp.setText("测试分享的文本 http://m.zhizaoyun.com/app/news/news.html");
//        sp.setImagePath("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1587638830408&di=72286457faf0bf14766b5ecc1f7bb31c&imgtype=0&src=http%3A%2F%2Fbiaoqingba.cn%2Fwp-content%2Fuploads%2F2018%2F03%2Fcdf7475427f1807-3.jpeg");
//        Platform qzone = ShareSDK.getPlatform (SinaWeibo.NAME);
//// 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
//        qzone.setPlatformActionListener (new PlatformActionListener() {
//            public void onError(Platform arg0, int arg1, Throwable arg2) {
//                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
//                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
//            }
//            public void onComplete(Platform arg0, int arg1, HashMap arg2) {
//                //分享成功的回调
//                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
//            }
//            public void onCancel(Platform arg0, int arg1) {
//                //取消分享的回调
//                Toast.makeText(context, "用户取消分享", Toast.LENGTH_SHORT).show();
//            }
//        });
//// 执行图文分享
//        qzone.share(sp);
    }

    public void WechatMomentsshowShare() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(shareSdkBean.getTitle());
        sp.setTitleUrl(shareSdkBean.getUrl()); // 标题的超链接
        sp.setText(shareSdkBean.getTxt());
        sp.setImageUrl(shareSdkBean.getIcon());
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
        sp.setTitle(shareSdkBean.getTitle());
        sp.setTitleUrl(shareSdkBean.getUrl()); // 标题的超链接
        sp.setText(shareSdkBean.getTxt());
        sp.setImageUrl(shareSdkBean.getIcon());
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
}
