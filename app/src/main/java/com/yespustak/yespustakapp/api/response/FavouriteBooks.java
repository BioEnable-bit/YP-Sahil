package com.yespustak.yespustakapp.api.response;

import com.yespustak.yespustakapp.models.BookModel;

import java.util.List;

public class FavouriteBooks extends BaseResponse {

    //    @SerializedName("result")
    List<BookModel> result;

    public List<BookModel> getResult() {
        return result;
    }
}
