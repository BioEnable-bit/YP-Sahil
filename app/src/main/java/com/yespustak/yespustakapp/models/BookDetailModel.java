package com.yespustak.yespustakapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookDetailModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("heading")
    @Expose
    private String heading;
    @SerializedName("ncrt_boook_flag")
    @Expose
    private Integer ncrt_boook_flag;
    @SerializedName("isbn")
    @Expose
    private String isbn;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("page_count")
    @Expose
    private String pageCount;
    @SerializedName("book_class")
    @Expose
    private String bookClass;
    @SerializedName("board_name")
    @Expose
    private String boardName;
    @SerializedName("book_description")
    @Expose
    private String bookDescription;
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("favourite")
//    @Expose
    private boolean favourite;

    @SerializedName("image_url_1")
    @Expose
    private String imageUrl1;
    @SerializedName("image_url_2")
    @Expose
    private String imageUrl2;
    @SerializedName("publisher_name")
    @Expose
    private String publisherName;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("ypp")
    @Expose
    private String ypp;

    @SerializedName("book_file")
    @Expose
    private String book_file;

    public String getBook_file() {
        return book_file;
    }

    public void setBook_file(String book_file) {
        this.book_file = book_file;
    }

    private final static long serialVersionUID = 8525098876926088829L;

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

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Integer getNcrt_boook_flag() {
        return ncrt_boook_flag;
    }

    public void setNcrt_boook_flag(Integer ncrt_boook_flag) {
        this.ncrt_boook_flag = ncrt_boook_flag;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getBookClass() {
        return bookClass;
    }

    public void setBookClass(String bookClass) {
        this.bookClass = bookClass;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getYpp() {
        return ypp;
    }

    public void setYpp(String ypp) {
        this.ypp = ypp;
    }

}