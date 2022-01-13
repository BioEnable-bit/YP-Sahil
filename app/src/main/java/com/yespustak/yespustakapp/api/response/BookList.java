package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.BookModel;

import java.util.List;

public class BookList extends BaseResponse {

    @SerializedName("result")
    List<BookModel> bookListModels;

    public List<BookModel> getBookListModels() {
        return bookListModels;
    }

}
