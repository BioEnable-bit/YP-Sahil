package com.yespustak.yespustakapp.models;

public class NotificationModel {

    String imgUrl, title, Desc;

    public NotificationModel(String imgUrl, String title, String desc) {
        this.imgUrl = imgUrl;
        this.title = title;
        Desc = desc;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }
}
