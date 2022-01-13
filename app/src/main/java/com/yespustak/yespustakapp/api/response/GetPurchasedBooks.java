package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.DownloadBook;

import java.util.List;

public class GetPurchasedBooks extends BaseResponse {

    @SerializedName("result")
    List<DownloadBook> purchasedBooks;

    public List<DownloadBook> getPurchasedBooks() {
        return purchasedBooks;
    }
}
