package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.BookDetailModel;

import java.util.List;

public class BookDetail {

    @SerializedName("result")
    BookDetailModel bookDetailModels;

    public BookDetailModel getBookDetailModels() {
        return bookDetailModels;
    }
}
