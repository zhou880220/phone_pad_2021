package com.example.honey_create_cloud.bean;

/**
 * Created by wangpan on 2020/5/13
 */
public class WxPayBean {

    /**
     * code : 200
     * data : {"appid":"wx5b3f59728cb6aa71","noncestr":"j7wTom920NEoRHy8","partnerid":"1568721801","prepayid":"wx13110716077309cc0bb357c11275093500","sign":"8A515519AAEEE3510FA1F0C99A767C63","timestamp":"1589339238021","wxPackage":"Sign=WXPay"}
     * msg : 请求成功
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * appid : wx5b3f59728cb6aa71
         * noncestr : j7wTom920NEoRHy8
         * partnerid : 1568721801
         * prepayid : wx13110716077309cc0bb357c11275093500
         * sign : 8A515519AAEEE3510FA1F0C99A767C63
         * timestamp : 1589339238021
         * wxPackage : Sign=WXPay
         */

        private String appid;
        private String noncestr;
        private String partnerid;
        private String prepayid;
        private String sign;
        private String timestamp;
        private String wxPackage;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getWxPackage() {
            return wxPackage;
        }

        public void setWxPackage(String wxPackage) {
            this.wxPackage = wxPackage;
        }
    }
}
