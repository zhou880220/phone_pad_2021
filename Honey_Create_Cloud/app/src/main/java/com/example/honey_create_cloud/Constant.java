package com.example.honey_create_cloud;

//常量类
public class Constant {
    //  https://njtestyyzx.zhizaoyun.com/home  测试地址


    //本地路径Url 示例
    public static final String fileAssetsUrl = "file:///android_asste/...";

    //入口
    public static final String test_fileAssetsUrl = "https://172.16.23.210:3006/src/view/example/purchaseOfEntry.html";
    // 测试环境桶名
    public static final String test_bucket_Name = "njdeveloptest";
    // 生产环境桶名
    public static final String prod_bucket_Name = "honeycom-service";

    //接口调用
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx5b3f59728cb6aa71"; //微信支付ID
    public static final String locahost_url = "https://njtestyyzx.zhizaoyun.com/cashierDesk"; //路径前缀
    public static final String text_url = "https://njtestyyzx.zhizaoyun.com/home"; //用户首页  172.16.23.67:3002    https://njtestyyzx.zhizaoyun.com/home
    public static final String login_url = "https://njtestyyzx.zhizaoyun.com/login"; //用户首页  172.16.23.67:3002    https://njtestyyzx.zhizaoyun.com/home
    public static final String apply_url = "https://njtestyyzx.zhizaoyun.com/apply"; //用户中心  172.16.23.67:3002    https://njtestyyzx.zhizaoyun.com/home
    public static final String Apply_Details = "https://njtesthoneycomb.zhizaoyun.com/gateway/api-apps/client/recentlyApps?equipmentId=3&userId="; //获取悬浮窗应用
    public static final String upload_multifile = "https://njtesthoneycomb.zhizaoyun.com/gateway/api-f/upload/multifile"; //上传图片
    public static final String headPic = "https://njtesthoneycomb.zhizaoyun.com/gateway/api-u/headPic"; //获取头像是否修改成功
    public static final String TAKE_PHOTO = "https://njtesthoneycomb.zhizaoyun.com/gateway/api-f/download/getFileUrl";//获取头像URL
    public static final String appOrderInfo = "https://njtesthoneycomb.zhizaoyun.com/gateway/api-pay/aliPay/appOrderInfo/"; //获取支付宝订单详情
    public static final String wxPay_appOrderInfo = "https://njtesthoneycomb.zhizaoyun.com/gateway/api-pay/wxPay/appOrderInfo/"; //获取微信订单详情
    public static final String payType = "https://njtesthoneycomb.zhizaoyun.com/gateway/api-apps/client/order/user/payType";  //获取用户支付类型，订单号，用户id
    public static final String test_shoppingCart = "https://njtestyyzx.zhizaoyun.com/shoppingCart"; //支付页面订单列表
    public static final String NOTICE_OPEN_SWITCH = "https://njtesthoneycomb.zhizaoyun.com/gateway/api-n/notification-anon/client/notice/status"; //开启或关闭用户通知接口
    public static final String MyOrderList = "https://njtestyyzx.zhizaoyun.com/myOrder";//订单列表
    public static final String TOKEN_IS_OK = "https://njtesthoneycomb.zhizaoyun.com/gateway/api-u/users/current?access_token=";//token是否有效
    public static final String DELETE_QUEUE = "https://njtesthoneycomb.zhizaoyun.com/gateway/api-n/notification-anon/queue/delete?userId=";//用户登录删除队列
}
