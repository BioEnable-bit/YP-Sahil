package com.yespustak.yespustakapp.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "notes")
public class NoteModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    private String title;
    private String description;

    @ColumnInfo(name = "tag_color")
    private int tagColor;

    private boolean isPinned;

    @ColumnInfo(name = "book_id")
    private int bookId;

    @ColumnInfo(name = "book_page_no")
    private int book_page_no;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public NoteModel() {
        this.createdAt = new Date(System.currentTimeMillis());
    }

    public NoteModel(String title, String description) {
        super();
        this.title = title;
        this.description = description;
    }

    public NoteModel(String title, String description, boolean isPinned) {
        super();
        this.title = title;
        this.description = description;
        this.isPinned = isPinned;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTagColor() {
        return tagColor;
    }

    public void setTagColor(int tagColor) {
        this.tagColor = tagColor;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public int getBook_page_no() {
        return book_page_no;
    }

    public void setBook_page_no(int book_page_no) {
        this.book_page_no = book_page_no;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
