package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.UserModel;

public class GetUserProfile extends BaseResponse {

    @SerializedName("user")
    private UserModel user;

    public UserModel getUser() {
        return user;
    }
}
