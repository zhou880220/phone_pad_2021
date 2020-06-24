package com.example.honey_create_cloud.bean;

/**
 * Created by wangpan on 2020/5/22
 */
public class NotificationBean {


    /**
     * title :
     * content :
     * userId :
     * unReadMessageCount : 1
     */


    private String title;
    private String content;
    private String userId;
    private int unReadMessageCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUnReadMessageCount() {
        return unReadMessageCount;
    }

    public void setUnReadMessageCount(int unReadMessageCount) {
        this.unReadMessageCount = unReadMessageCount;
    }
}
