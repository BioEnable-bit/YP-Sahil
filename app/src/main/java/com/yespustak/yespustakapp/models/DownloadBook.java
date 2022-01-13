package com.yespustak.yespustakapp.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.tonyodev.fetch2.Status;

import java.io.Serializable;

/**
 * using this model for Room and retrofit
 */
@Entity(tableName = "download_books")
public class DownloadBook implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("aaa") //renamed to something random to avoid conflict with id
    private int id;

    @SerializedName("id")
    private int rid;

    @SerializedName("title")
    private String title;

    @SerializedName("publication")
    private String publication;

    @SerializedName("img_url")
    private String imgUrl;

    @SerializedName("file_url")
    private String fileUrl;

    @SerializedName("password")
    private String password;

//    private String fileHash;
//    @ColumnInfo(name = "pdf_url")
    private long totalBytes;
    private long downloadedBytes;
    private long etaInMilliSeconds;
    private int progress;
    private Status status;
    private String speed;
    private String fileUri;

    public DownloadBook() {
    }

    @Ignore
    public DownloadBook(String title, String publication, String fileUrl, String imgUrl) {
        this.title = title;
        this.publication = publication;
        this.fileUrl = fileUrl;
        this.imgUrl = imgUrl;
    }

    @Ignore
    public DownloadBook(String title, String description, long totalBytes, int progress, String imgUrl, String fileUrl) {
        this.title = title;
        this.publication = description;
        this.totalBytes = totalBytes;
        this.progress = progress;
        this.imgUrl = imgUrl;
        this.fileUrl = fileUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    public long getEtaInMilliSeconds() {
        return etaInMilliSeconds;
    }

    public void setEtaInMilliSeconds(long etaInMilliSeconds) {
        this.etaInMilliSeconds = etaInMilliSeconds;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "DownloadBook{" +
                "id=" + id +
                ", rid=" + rid +
                ", title='" + title + '\'' +
                ", publication='" + publication + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", password='" + password + '\'' +
                ", size=" + totalBytes +
                ", progress=" + progress +
                ", status=" + status +
                ", speed='" + speed + '\'' +
                ", fileUri='" + fileUri + '\'' +
                '}';
    }
}
