package com.example.honey_create_cloud;

//常量类
public class Constant {
    /**
     * 测试环境前缀
     * 页面前缀 ：http://njtestyyzx.zhizaoyun.com/
     * 接口前缀 ：http://njtesthoneycomb.zhizaoyun.com/gateway/
     */
    public static final String TEST_PAGE_URL = "http://njtestyyzx.zhizaoyun.com/";
    public static final String TEST_INTERFACE_URL = "http://njtesthoneycomb.zhizaoyun.com/gateway/";

    /**
     * 生产环境前缀
     * 页面前缀 ：http://mobileclient.zhizaoyun.com/
     * 接口前缀 ：http://ulogin.zhizaoyun.com/gateway/
     */
    public static final String PRODUCTION_PAGE_URL = "http://mobileclient.zhizaoyun.com/";
    public static final String PRODUCTION_INTERFACE_URL = "http://ulogin.zhizaoyun.com/gateway/";

    /**
     * 演示环境前缀
     * 页面前缀 ：https://mobileclientthird.zhizaoyun.com/
     * 接口前缀 ：https://mobileclientthird.zhizaoyun.com/gateway/
     */
    public static final String DEMONSTRAION_PAGE_URL = "https://mobileclientthird.zhizaoyun.com/";
    public static final String DEMONSTRAION_INTERFACE_URL = "https://mobileclientthird.zhizaoyun.com/gateway/";

    // 测试环境桶名
    public static final String test_bucket_Name = "njdeveloptest";
    // 生产环境桶名
    public static final String prod_bucket_Name = "honeycom-service";

    ///接口调用
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx5b3f59728cb6aa71"; //微信支付ID
    //以下为页面前缀
    public static final String locahost_url = TEST_PAGE_URL+"cashierDesk"; //路径前缀
    public static final String text_url = TEST_PAGE_URL+"home"; //用户首页
    public static final String login_url = TEST_PAGE_URL+"login"; //登录页
    public static final String apply_url = TEST_PAGE_URL+"apply"; //用户中心
    public static final String register_url = TEST_PAGE_URL+"register"; //用户注册
    public static final String APP_NOTICE_LIST = TEST_PAGE_URL+"appNoticeList"; //消息页
    public static final String MyOrderList = TEST_PAGE_URL+"myOrder";//订单列表
    public static final String test_shoppingCart = TEST_PAGE_URL+"shoppingCart"; //支付页面订单列表
    //以下为接口前缀
    public static final String Apply_Details = TEST_INTERFACE_URL+"api-apps/client/recentlyApps?equipmentId=3&userId="; //获取悬浮窗应用
    public static final String upload_multifile = TEST_INTERFACE_URL+"api-f/upload/multifile"; //上传图片
    public static final String headPic = TEST_INTERFACE_URL+"api-u/headPic"; //获取头像是否修改成功
    public static final String TAKE_PHOTO = TEST_INTERFACE_URL+"api-f/download/getFileUrl";//获取头像URL
    public static final String appOrderInfo = TEST_INTERFACE_URL+"api-pay/aliPay/appOrderInfo/"; //获取支付宝订单详情
    public static final String wxPay_appOrderInfo = TEST_INTERFACE_URL+"api-pay/wxPay/appOrderInfo/"; //获取微信订单详情
    public static final String payType = TEST_INTERFACE_URL+"api-apps/client/order/user/payType";  //获取用户支付类型，订单号，用户id
    public static final String NOTICE_OPEN_SWITCH = TEST_INTERFACE_URL+"api-n/notification-anon/client/notice/status"; //开启或关闭用户通知接口
    public static final String TOKEN_IS_OK = TEST_INTERFACE_URL+"api-u/users/current?access_token=";//token是否有效
    public static final String DELETE_QUEUE = TEST_INTERFACE_URL+"api-n/notification-anon/queue/delete?userId=";//用户登录删除队列
    public static final String GETAPPLY_URL = TEST_INTERFACE_URL+"api-apps/operation/apps-anon/interfaceUrl?appId="; //获取当前三方应用首页链接
}
