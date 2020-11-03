package com.example.honey_create_cloud_pad.bean;

/**
 * Created by wangpan on 2020/8/14
 * 查讯应用标题名称
 */
public class TitleName {

    /**
     * code : 200
     * count : 0
     * data : 资源测试
     * msg : 应用名称查询成功
     */

    private int code;
    private int count;
    private String data;
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
