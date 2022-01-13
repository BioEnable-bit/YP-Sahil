package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;

public class GetUserPref extends BaseResponse {

    @SerializedName("userpreferences")
    UserPref userPref;

    public UserPref getUserPref() {
        return userPref;
    }
}
