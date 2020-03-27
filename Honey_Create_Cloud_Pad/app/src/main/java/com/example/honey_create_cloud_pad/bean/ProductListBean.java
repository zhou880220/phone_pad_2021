package com.example.honey_create_cloud_pad.bean;

/**
 * Created by wangpan on 2020/3/9
 */

import java.io.Serializable;


/**
 * 实体类
 */

public class ProductListBean implements Serializable {
    private String proName;
    private int imgUrl;

    public ProductListBean(String proName, int imgUrl) {
        this.proName = proName;
        this.imgUrl = imgUrl;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public int getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(int imgUrl) {
        this.imgUrl = imgUrl;
    }
}

