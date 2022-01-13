package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;

public class SavePaymentResponse extends BaseResponse{

    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
