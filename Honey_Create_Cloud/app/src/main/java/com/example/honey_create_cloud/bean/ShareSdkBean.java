package com.example.honey_create_cloud.bean;

/**
 * Created by wangpan on 2020/4/29
 */
public class ShareSdkBean {

    /**
     * title : 分享标题
     * txt : 测试分享文本
     * url : http://www.baidu.com
     * icon : http://172.16.23.69:3006/src/img/favicon.ico
     */

    private String title;
    private String txt;
    private String url;
    private String icon;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
