package com.example.honey_create_cloud.bean;

/**
 * Created by wangpan on 2020/4/27  获取订单详情
 */
public class AppOrderInfo {

    /**
     * code : 200
     * data : alipay_root_cert_sn=687b59193f3f462dd5336e5abf83c5d8_02941eef3187dddf3d3b83462e1dfcf6&alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_cert_sn=3aa5e5d46a86bdfcbfc4cd75b9b61863&app_id=2021000199673968&biz_content=%7B%22subject%22%3A%22%E6%B5%8B%E8%AF%95%E6%A0%87%E9%A2%98%22%2C%22body%22%3A%22%22%2C%22timeout_express%22%3A%2230m%22%2C%22out_trade_no%22%3A%222020888881%22%2C%22total_amount%22%3A%220.01%22%7D&charset=utf-8&format=JSON&method=alipay.trade.app.pay&sign=Lr5AeN5UGbaZCxb9lCwKTGbwsbrIdPPsMhIupy2UISPdd7gchNYYeW6pAy1tePfUS3CEwLOJkBqkBFtY3Ny3ntTRfgvfSQom6Xv1WPp%2BdV2FWNkLw4WCV4LJp5Tqji%2FVue1A5SJAEwSGaP5UvKmV1vb2CXx5i1HVwzwQ5itU%2BIP1a71Gwsg0gNvp39f4rRLoqV5%2BonNmrNdL3bB%2BOIKh0xd4wcwoe3m%2BnGj%2F6xEaPBdboP%2BNXwEpU7%2Fjthr3wdOBtYxo5ta2%2BwKtv8zaeXDOgsU%2F7ZCSe9tXytrhCiXqeqgDDrOCEHMvfIUI%2FXBILLA6LRg77NYhZINE%2Bcr8XGqB3Q%3D%3D&sign_type=RSA2&timestamp=2020-04-27+15%3A43%3A39&version=1.0
     * msg : 请求成功
     */

    private int code;
    private String data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
