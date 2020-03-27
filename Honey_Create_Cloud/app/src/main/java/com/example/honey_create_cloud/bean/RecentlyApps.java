package com.example.honey_create_cloud.bean;

import java.util.List;

/**
 * Created by wangpan on 2020/3/24
 */
public class RecentlyApps {


    /**
     * code : 200
     * count : 0
     * data : [{"appId":56,"appInterfaceUrl":"http://www.zhizaoyun.wang/","appName":"测试平台","logoUrl":"https://njdeveloptest.obs.cn-east-2.myhuaweicloud.com:443/app/d5c8ac0b7b294d55b2bb8bca7af19be2.jpg?AccessKeyId=CLX6MAVTXA1WBLKJFVI7&Expires=1585121823&Signature=fx01ZJmopv2GEKa2kbv90i%2BiQ1M%3D","openTime":"2020-03-25 14:36:28"},{"appId":39,"appInterfaceUrl":"http://www.zhizaoyun.wang/","appName":"图纸通","logoUrl":"https://njdeveloptest.obs.cn-east-2.myhuaweicloud.com:443/app/4a0c90910a744b2293c7c67b1514cc7a.jpg?AccessKeyId=CLX6MAVTXA1WBLKJFVI7&Expires=1585121823&Signature=IKalEEnc0R58XlEYiwvqxh06Los%3D","openTime":"2020-03-25 14:04:31"}]
     * msg : 用户最近使用的app信息查询成功！
     */

    private int code;
    private int count;
    private String msg;
    private List<DataBean> data;

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
         * appId : 56
         * appInterfaceUrl : http://www.zhizaoyun.wang/
         * appName : 测试平台
         * logoUrl : https://njdeveloptest.obs.cn-east-2.myhuaweicloud.com:443/app/d5c8ac0b7b294d55b2bb8bca7af19be2.jpg?AccessKeyId=CLX6MAVTXA1WBLKJFVI7&Expires=1585121823&Signature=fx01ZJmopv2GEKa2kbv90i%2BiQ1M%3D
         * openTime : 2020-03-25 14:36:28
         */

        private int appId;
        private String appInterfaceUrl;
        private String appName;
        private String logoUrl;
        private String openTime;

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public String getAppInterfaceUrl() {
            return appInterfaceUrl;
        }

        public void setAppInterfaceUrl(String appInterfaceUrl) {
            this.appInterfaceUrl = appInterfaceUrl;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public String getOpenTime() {
            return openTime;
        }

        public void setOpenTime(String openTime) {
            this.openTime = openTime;
        }
    }
}
