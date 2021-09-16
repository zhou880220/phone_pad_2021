package com.example.honey_create_cloud.util;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.ShareSdkBean;
import com.example.honey_create_cloud.ui.ApplyFirstActivity;
import com.google.gson.Gson;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.encoder.QRCode;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//import com.yzq.zxinglibrary.encode.CodeCreator;

import java.util.HashMap;

/**
 * Created by wangpan on 2020/4/27
 */
public class ShareSDK_Web {
    private Context context;
    private String content;
    private ShareSdkBean shareSdkBean;
    private IWXAPI wxApi;

    public ShareSDK_Web(Context context, String content) {
        this.context = context;
        this.content = content;
        init();
    }
//
    private void init() {
        Gson gson = new Gson();
        shareSdkBean = gson.fromJson(content, new ShareSdkBean().getClass());
        wxApi = WXAPIFactory.createWXAPI(context, Constant.APP_ID);
        wxApi.registerApp(Constant.APP_ID);
    }

    public void QQshowShare() {

    }

    public void QRcode() {

    }

    public void CopyUrl() {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", shareSdkBean.getUrl());
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        Toast.makeText(context, "已复制到剪切板", Toast.LENGTH_SHORT).show();
    }

    public void WechatMomentsshowShare() {

    }

    /**
     *
     * @param flag (0:分享到微信好友，1：分享到微信朋友圈)
     */
    public void wechatShare(int flag){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://www.baidu.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "这里填写标题";
        msg.description = "这里填写内容";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.wechat);
        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    public void WechatshowShare() {

    }
}
