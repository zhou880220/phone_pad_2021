package com.example.honey_create_cloud.bean;

/**
 * Created by wangpan on 2020/10/21
 */
public class RabbitMQBean {

    /**
     * code : 200
     * count : 0
     * data : {"mqAddress":"122.112.220.117","mqPassword":"honeycomb","mqPort":"5672","mqUser":"honeycomb","mqVirtualHost":"/"}
     * msg : RabbitMq信息查询成功
     */

    private int code;
    private int count;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
         * mqAddress : 122.112.220.117
         * mqPassword : honeycomb
         * mqPort : 5672
         * mqUser : honeycomb
         * mqVirtualHost : /
         */

        private String mqAddress;
        private String mqPassword;
        private String mqPort;
        private String mqUser;
        private String mqVirtualHost;

        public String getMqAddress() {
            return mqAddress;
        }

        public void setMqAddress(String mqAddress) {
            this.mqAddress = mqAddress;
        }

        public String getMqPassword() {
            return mqPassword;
        }

        public void setMqPassword(String mqPassword) {
            this.mqPassword = mqPassword;
        }

        public String getMqPort() {
            return mqPort;
        }

        public void setMqPort(String mqPort) {
            this.mqPort = mqPort;
        }

        public String getMqUser() {
            return mqUser;
        }

        public void setMqUser(String mqUser) {
            this.mqUser = mqUser;
        }

        public String getMqVirtualHost() {
            return mqVirtualHost;
        }

        public void setMqVirtualHost(String mqVirtualHost) {
            this.mqVirtualHost = mqVirtualHost;
        }
    }
}
