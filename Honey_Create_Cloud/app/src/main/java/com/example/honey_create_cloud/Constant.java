package com.example.honey_create_cloud;

//常量类
public class Constant {

    public static final String Attention = "http://139.9.172.71/yyzx/";

    //本地应用中心测试网址路径  http://172.16.23.69:3001
    public static final String text_url = "http://njtestyyzx.zhizaoyun.com/home"; //http://172.16.23.69:3001/home?id=123
    //本地路径Url 示例
    public static final String fileAssetsUrl = "file:///android_asste/...";


    //入口
    public static final String test_fileAssetsUrl = "http://172.16.23.210:3006/src/view/example/purchaseOfEntry.html";
    //支付页面订单列表
    public static final String test_shoppingCart = "http://njtestyyzx.zhizaoyun.com/shoppingCart";


    //接口调用
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx5b3f59728cb6aa71";
    public static final String Apply_Details = "http://139.9.172.71:18080/api-apps/client/recentlyApps?equipmentId=3&userId="; //获取悬浮窗应用
    public static final String upload_multifile = "http://139.9.172.71:18080/api-f/upload/multifile"; //上传图片
    public static final String headPic = "http://139.9.172.71:18080/api-u/headPic"; //获取头像是否修改成功
    public static final String appOrderInfo = "http://hawk.devtest.zhizaoyun.com/aliPay/appOrderInfo/"; //获取支付宝订单详情
    public static final String wxPay_appOrderInfo = "http://hawk.devtest.zhizaoyun.com/wxPay/appOrderInfo/"; //获取微信订单详情
    public static final String payType = "http://139.9.172.71:18080/api-apps/client/order/user/payType";  //获取用户支付类型，订单号，用户id

}
