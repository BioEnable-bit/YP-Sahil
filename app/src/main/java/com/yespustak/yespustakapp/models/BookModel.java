package com.yespustak.yespustakapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookModel {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("subject")
    @Expose
    private String subject;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("publication")
    @Expose
    private String publication;

    @SerializedName("mrp")
    @Expose
    private double mrp;

    @SerializedName("ypp")
    @Expose
    private double ypp;

    private int cardBgDrawable, cardShadowColor;

    public BookModel() {
    }

    public BookModel(Integer id, String subject, String title, String imageUrl, String publication, double mrp, double ypp) {
        this.id = id;
        this.subject = subject;
        this.title = title;
        this.imageUrl = imageUrl;
        this.publication = publication;
        this.mrp = mrp;
        this.ypp = ypp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getYpp() {
        return ypp;
    }

    public void setYpp(double ypp) {
        this.ypp = ypp;
    }

    public int getCardBgDrawable() {
        return cardBgDrawable;
    }

    public void setCardBgDrawable(int cardBgDrawable) {
        this.cardBgDrawable = cardBgDrawable;
    }

    public int getCardShadowColor() {
        return cardShadowColor;
    }

    public void setCardShadowColor(int cardShadowColor) {
        this.cardShadowColor = cardShadowColor;
    }
}
