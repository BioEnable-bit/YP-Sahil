package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.BookModel;

import java.util.List;

public class RecommendationList extends BaseResponse {

    @SerializedName("result")
    List<BookModel> recommendationModelList;

    public List<BookModel> getRecommendationModelList() {
        return recommendationModelList;
    }
}
