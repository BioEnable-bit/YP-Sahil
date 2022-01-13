package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSections extends BaseResponse {

    @SerializedName("section")
    List<String> sections;

    public List<String> getSections() {
        return sections;
    }
}