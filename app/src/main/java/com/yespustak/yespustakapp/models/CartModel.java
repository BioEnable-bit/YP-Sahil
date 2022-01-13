package com.yespustak.yespustakapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CartModel {


    @SerializedName(value = "book_id", alternate = "id") //this is used temporary
    @Expose
    private Integer bookId;
    @SerializedName("book_title")
    @Expose
    private String bookTitle;
    @SerializedName("book_mrp")
    @Expose
    private double bookMrp;
    @SerializedName("book_ypp")
    @Expose
    private double bookYpp;
    @SerializedName("book_subject")
    @Expose
    private String bookSubject;
    @SerializedName("book_standard")
    @Expose
    private String bookStandard;
    @SerializedName("book_front_image_url")
    @Expose
    private String bookFrontImageUrl;

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public double getBookYpp() {
        return bookYpp;
    }

    public void setBookYpp(double bookYpp) {
        this.bookYpp = bookYpp;
    }

    public String getBookSubject() {
        return bookSubject;
    }

    public void setBookSubject(String bookSubject) {
        this.bookSubject = bookSubject;
    }

    public String getBookStandard() {
        return bookStandard;
    }

    public void setBookStandard(String bookStandard) {
        this.bookStandard = bookStandard;
    }

    public String getBookFrontImageUrl() {
        return bookFrontImageUrl;
    }

    public void setBookFrontImageUrl(String bookFrontImageUrl) {
        this.bookFrontImageUrl = bookFrontImageUrl;
    }

    public double getBookMrp() {
        return bookMrp;
    }

    public void setBookMrp(double bookMrp) {
        this.bookMrp = bookMrp;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
}
