package com.example.honey_create_cloud.bean;

import java.util.List;

/**
 * Created by wangpan on 2020/5/28
 */
public class TakePhoneBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : [{"rowNum":1,"fileName":"3B9B1E217F86D5E493FCE81A5B800770","fileUrl":"https://njdeveloptest.obs.cn-east-2.myhuaweicloud.com:443/menu/3B9B1E217F86D5E493FCE81A5B800770?AccessKeyId=CLX6MAVTXA1WBLKJFVI7&Expires=1590663970&Signature=9HuDNTgYpt9tz4xUvISvxc4wbPY%3D"}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * rowNum : 1
         * fileName : 3B9B1E217F86D5E493FCE81A5B800770
         * fileUrl : https://njdeveloptest.obs.cn-east-2.myhuaweicloud.com:443/menu/3B9B1E217F86D5E493FCE81A5B800770?AccessKeyId=CLX6MAVTXA1WBLKJFVI7&Expires=1590663970&Signature=9HuDNTgYpt9tz4xUvISvxc4wbPY%3D
         */

        private int rowNum;
        private String fileName;
        private String fileUrl;

        public int getRowNum() {
            return rowNum;
        }

        public void setRowNum(int rowNum) {
            this.rowNum = rowNum;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }
}
