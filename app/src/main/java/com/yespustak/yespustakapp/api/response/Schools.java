package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.SchoolModel;

import java.util.List;

public class Schools extends BaseResponse {

    @SerializedName("schools")
    List<SchoolModel> schoolModels;

    public List<SchoolModel> getSchoolModels() {
        return schoolModels;
    }
}