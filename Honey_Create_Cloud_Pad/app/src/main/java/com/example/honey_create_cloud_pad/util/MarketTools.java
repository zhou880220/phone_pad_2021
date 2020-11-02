package com.example.honey_create_cloud_pad.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangpan on 2020/8/28
 * com.tencent.android.qqdownloader 腾讯应用宝
 * com.qihoo.appstore 360手机助手
 * com.baidu.appsearch 百度手机助手
 * com.xiaomi.market 小米应用商店
 * com.huawei.appmarket 华为应用商店
 * com.wandoujia.phoenix2 豌豆荚
 * com.dragon.android.pandaspace 91手机助手
 * com.hiapk.marketpho 安智应用商店
 * com.yingyonghui.market 应用汇
 * com.tencent.qqpimsecure QQ手机管家
 * com.mappn.gfan 机锋应用市场
 * com.pp.assistant PP手机助手
 * com.oppo.market OPPO应用商店
 * cn.goapk.market GO市场
 * zte.com.market 中兴应用商店
 * com.yulong.android.coolmart 宇龙Coolpad应用商店
 * com.lenovo.leos.appstore 联想应用商店
 * com.coolapk.market cool市场
 * com.bbk.appstore VIVO应用商店
 * com.taobao.appcenter -----淘宝手机助手
 */
public class MarketTools {

    public static void apphomelist(Context context){
        List<String> pages = new ArrayList<>();//创建一个list  把上传应用的应用商店的包名填在list中遍历
        pages.add("com.tencent.android.qqdownloader");//腾讯应用宝
        pages.add("com.xiaomi.market");//小米应用商店
        pages.add("com.huawei.appmarket");//华为应用商店
        pages.add("com.oppo.market");//OPPO应用商店
        pages.add("com.taobao.appcenter");//淘宝手机助手
        pages.add("com.bbk.appstore");//VIVO应用商店
        for (int i = 0; i < pages.size(); i++) {
            if (isAvilible(context, pages.get(i)) == true) {   //如果返回true
                launchAppDetail(context, "com.example.honey_create_cloud", pages.get(i));//跳转到该应用商店的APP下载详情页面
                return;
            }else{
                Uri uri = Uri.parse("http://www.zhizaoyun.com/download.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
            if (i == 6){//    如果手机未安装市场应用，那么根据后台返回的地址，用浏览器打开地址进行下载
                Uri uri = Uri.parse("http://www.zhizaoyun.com/download.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        }
    }

    /**
     * 启动到应用商店app详情界面
     *
     * @param appPkg    目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面
     */
    public static void launchAppDetail(Context mContext, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) {
                return;
            }

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断应用市场是否存在的方法
     *
     * @param context
     * @param packageName 主流应用商店对应的包名
     *                    com.tencent.android.qqdownloader     -----应用宝
     *                    com.baidu.appsearch    -----百度手机助
     *                    com.xiaomi.market    -----小米应用商店
     *                    com.huawei.appmarket    -----华为应用市场
     *                    com.oppo.market      oppo
     *                    com.taobao.appcenter    -----淘宝手机助手
     *                    com.bbk.appstore         VIVO应用商店
     */
    public static boolean isAvilible(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> pName = new ArrayList<String>();
        // 从pinfo中将包名字取出
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pf = pinfo.get(i).packageName;
                pName.add(pf);
            }
        }
        // 判断pName中是否有目标程序的包名，有true，没有false
        return pName.contains(packageName);
    }
}
