package com.example.honey_create_cloud;

//常量类
public class Constant {
    /**
     * 测试环境前缀
     * 页面前缀 ：https://njtestyyzx.zhizaoyun.com/
     * 接口前缀 ：https://njtesthoneycomb.zhizaoyun.com/gateway/
     */
    public static final String PAGE_URL = "https://njtestyyzx.zhizaoyun.com/";
    public static final String INTERFACE_URL = "https://njtesthoneycomb.zhizaoyun.com/gateway/";

    /**
     * 生产环境前缀
     * 页面前缀 ：https://mobileclient.zhizaoyun.com/
     * 接口前缀 ：https://ulogin.zhizaoyun.com/gateway/
     */
//    public static final String PAGE_URL = "https://mobileclient.zhizaoyun.com/";
//    public static final String INTERFACE_URL = "https://ulogin.zhizaoyun.com/gateway/";

    /**
     * 调试环境前缀
     * 页面前缀 ：https://mobileclientthird.zhizaoyun.com/
     * 接口前缀 ：https://mobileclientthird.zhizaoyun.com/gateway/
     */
//    public static final String PAGE_URL = "http://172.16.23.59:3001/";//"https://njtestyyzx.zhizaoyun.com/";//"https://mobileclientthird.zhizaoyun.com/";
//    public static final String INTERFACE_URL = "http://172.16.14.231:18080/";//"https://mobileclientthird.zhizaoyun.com/gateway/";

    // 测试环境桶名
    public static final String bucket_Name = "njdeveloptest";
    // 生产环境桶名
//    public static final String bucket_Name = "honeycom-service";

    ///接口调用
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx5b3f59728cb6aa71"; //微信支付ID
    // QQ
    public static final String QQ_APP_ID = "1110555495";
    //以下为页面前缀
    public static final String locahost_url = PAGE_URL + "cashierDesk"; //路径前缀  "http://172.16.23.116:3001/"
    public static final String text_url = PAGE_URL + "home"; //用户首页 //"http://172.16.23.253:3001/"
//    public static final String text_url = "http://172.16.23.253:3001/"; //用户首页 //"http://172.16.23.253:3001/"
    public static final String login_url = PAGE_URL + "login"; //登录页
    public static final String apply_url = PAGE_URL + "apply"; //用户中心
    public static final String register_url = PAGE_URL + "register"; //用户注册
    public static final String APP_NOTICE_LIST = PAGE_URL + "appNoticeList"; //消息页
    public static final String MyOrderList = PAGE_URL + "myOrder";//订单列表
    public static final String MyNews = PAGE_URL + "news"; //咨询页面
    public static final String test_shoppingCart = PAGE_URL + "shoppingCart"; //支付页面订单列表
    public static final String bind_url = PAGE_URL + "bindPhone"; //绑定手机号码
    //以下为接口前缀      TEST_INTERFACE_URL = "https://njtesthoneycomb.zhizaoyun.com/gateway/";
    public static final String Apply_Details = INTERFACE_URL + "api-apps/client/recentlyApps?equipmentId=3&userId="; //获取悬浮窗应用
    public static final String upload_multifile = INTERFACE_URL + "api-f/upload/multifile"; //上传图片
    public static final String headPic = INTERFACE_URL + "api-u/headPic"; //获取头像是否修改成功
    public static final String TAKE_PHOTO = INTERFACE_URL + "api-f/download/getFileUrl";//获取头像URL
    public static final String appOrderInfo = INTERFACE_URL + "api-pay/aliPay/appOrderInfo/"; //获取支付宝订单详情
    public static final String wxPay_appOrderInfo = INTERFACE_URL + "api-pay/wxPay/appOrderInfo/"; //获取微信订单详情
    public static final String payType = INTERFACE_URL + "api-apps/client/order/user/payType";  //获取用户支付类型，订单号，用户id
    public static final String NOTICE_OPEN_SWITCH = INTERFACE_URL + "api-n/notification-anon/client/notice/status"; //开启或关闭用户通知接口
    public static final String TOKEN_IS_OK = INTERFACE_URL + "api-u/users/current?access_token=";//token是否有效
    public static final String DELETE_QUEUE = INTERFACE_URL + "api-n/notification-anon/queue/delete?userId=";//用户登录删除队列
    public static final String GETAPPLY_URL = INTERFACE_URL + "api-apps/operation/apps-anon/appName?appId="; //获取当前三方应用首页链接
    public static final String GETRabbitMQAddress = INTERFACE_URL+"api-apps/menu/apps-anon/rabbitMqInfo";//获取RabbitMq推送服务地址
    public static final String userPushRelation = INTERFACE_URL+"api-msg/userPushRelation";//第一次打开应用保存
    public static final String userFirstUpdate = INTERFACE_URL+"api-msg/userPushRelation/firstUpdate";//用户第一次登录
    public static final String userPushRelationUpdate = INTERFACE_URL+"api-msg/userPushRelation/updateInfo";//用户退出
    public static final String WEBVERSION = INTERFACE_URL+"api-apps/apps-anon/client/version/details?equipmentId=3&updateVersion=";//apk升级功能  api-apps/
    public static final String APP_AUTH_CHECK = INTERFACE_URL+ "api-apps/apps-anon/client/platformPermissionAndPutaway";
    public static final String APP_INSTALL_TIMES = INTERFACE_URL+"api-apps/apps-anon/phone/install";


    public static final String NO_AUTH_TIP = "您的企业暂未开通此应用，请联系企业管理页开通后再试。";
    public static final String ERROR_SERVER_TIP = "平台服务器出现未知异常。";

    //手机类型(2：huawei；3：mi；4：oppo；5：vivo; 6:其他手机)
    public static final String PHONE_TYPE = "PHONE_TYPE";
    public static final String HAS_INSTALL = "has_install";
    public static final int PHONE_TYPE_HW = 2;
    public static final int PHONE_TYPE_MI = 3;
    public static final int PHONE_TYPE_OP = 4;
    public static final int PHONE_TYPE_VO = 5;
    public static final int PHONE_TYPE_OTHER = 6;



}
