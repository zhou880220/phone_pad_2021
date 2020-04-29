package com.example.honey_create_cloud.bean;

/**
 * Created by wangpan on 2020/3/24  修改头像
 */
public class HeadPic {

    /**
     * code : 200
     * data :
     * msg : 头像修改成功！
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
