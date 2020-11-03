package com.example.honey_create_cloud_pad.bean;

import java.util.List;

/**
 * Created by wangpan on 2020/6/4
 */
public class TokenIsOkBean {

    /**
     * code : 200
     * data : {"companyId":"","companyStatus":"","headImgUrl":"","isIdentification":"","permissions":[],"phone":"18612520011","remarks":"","role":"","sex":3,"status":0,"sysRoles":[],"userid":"0D9370562E921B8ED64580A8C615DB84","username":"wp2019"}
     * msg : 请求成功
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
         * companyId :
         * companyStatus :
         * headImgUrl :
         * isIdentification :
         * permissions : []
         * phone : 18612520011
         * remarks :
         * role :
         * sex : 3
         * status : 0
         * sysRoles : []
         * userid : 0D9370562E921B8ED64580A8C615DB84
         * username : wp2019
         */

        private String companyId;
        private String companyStatus;
        private String headImgUrl;
        private String isIdentification;
        private String phone;
        private String remarks;
        private String role;
        private int sex;
        private int status;
        private String userid;
        private String username;
        private List<?> permissions;
        private List<?> sysRoles;

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getCompanyStatus() {
            return companyStatus;
        }

        public void setCompanyStatus(String companyStatus) {
            this.companyStatus = companyStatus;
        }

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        public String getIsIdentification() {
            return isIdentification;
        }

        public void setIsIdentification(String isIdentification) {
            this.isIdentification = isIdentification;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<?> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<?> permissions) {
            this.permissions = permissions;
        }

        public List<?> getSysRoles() {
            return sysRoles;
        }

        public void setSysRoles(List<?> sysRoles) {
            this.sysRoles = sysRoles;
        }
    }
}
