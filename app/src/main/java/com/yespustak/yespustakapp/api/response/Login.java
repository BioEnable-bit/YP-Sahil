package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;

public class Login extends BaseResponse {

    @SerializedName("auth_token")
    String authToken;

    public String getAuthToken() {
        return authToken;
    }
}
