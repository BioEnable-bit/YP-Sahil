package com.yespustak.yespustakapp.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "notice")
public class NoticeModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "created_at")
    private String created_at;

    @ColumnInfo(name = "notice_url")
    private String notice_url;

    public NoticeModel(int id, String title, String created_at, String notice_url) {
        this.id = id;
        this.title = title;
        this.created_at = created_at;
        this.notice_url = notice_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getNotice_url() {
        return notice_url;
    }

    public void setNotice_url(String notice_url) {
        this.notice_url = notice_url;
    }
}
