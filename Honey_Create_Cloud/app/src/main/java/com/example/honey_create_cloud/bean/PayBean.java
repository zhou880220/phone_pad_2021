package com.example.honey_create_cloud.bean;

/**
 * Created by wangpan on 2020/4/15  获取页面传递的
 */
public class PayBean {


    /**
     * payType : 1
     * outTradeNo : id1
     * userId : 6666
     */

    private String payType;
    private String outTradeNo;
    private String userId;

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "PayBean{" +
                "payType='" + payType + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
