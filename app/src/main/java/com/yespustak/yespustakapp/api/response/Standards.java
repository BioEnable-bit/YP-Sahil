package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.StandardModel;

import java.util.List;

public class Standards extends BaseResponse {

    @SerializedName("standards")
    List<StandardModel> standardModels;

    public List<StandardModel> getStandardModels() {
        return standardModels;
    }
}
