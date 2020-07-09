package com.example.honey_create_cloud.bean;

/**
 * Created by wangpan on 2020/7/8
 */
public class QueryUrl {

    /**
     * code : 200
     * count : 0
     * data : http://njtestyyzx01.zhizaoyun.com/yyzx-jsapi/view/pc-example/testing.html
     * msg : 接入地址查询成功
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
