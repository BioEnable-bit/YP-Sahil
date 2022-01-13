package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Offers extends BaseResponse {

    @SerializedName("image_urls")
    List<String> imageUrls;

    public List<String> getImageUrls() {
        return imageUrls;
    }
}
