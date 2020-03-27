package com.example.honey_create_cloud_pad.bean;

/**
 * Created by wangpan on 2020/3/24
 */
public class Head {

    /**
     * userId : 4277FA170D1F545A64893FCB3BFD2824
     * url : b0b39983769c42b8ad8f7b35c35a6962.jpg
     */

    private String userId;
    private String url;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Head{" +
                "userId='" + userId + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
