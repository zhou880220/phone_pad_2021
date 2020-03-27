package com.example.honey_create_cloud_pad.bean;

import java.util.List;

/**
 * Created by wangpan on 2020/3/24
 */
public class PictureUpload {

    /**
     * code : 200
     * msg : 请求成功
     * data : [{"rowNum":1,"newName":"3003a0f6d7974a4989845e69768b3f17.png","oldName":"logo.png","suffixName":".png","folderName":"headPic"}]
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
         * newName : 3003a0f6d7974a4989845e69768b3f17.png
         * oldName : logo.png
         * suffixName : .png
         * folderName : headPic
         */

        private int rowNum;
        private String newName;
        private String oldName;
        private String suffixName;
        private String folderName;

        public int getRowNum() {
            return rowNum;
        }

        public void setRowNum(int rowNum) {
            this.rowNum = rowNum;
        }

        public String getNewName() {
            return newName;
        }

        public void setNewName(String newName) {
            this.newName = newName;
        }

        public String getOldName() {
            return oldName;
        }

        public void setOldName(String oldName) {
            this.oldName = oldName;
        }

        public String getSuffixName() {
            return suffixName;
        }

        public void setSuffixName(String suffixName) {
            this.suffixName = suffixName;
        }

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }
    }
}
