package com.yespustak.yespustakapp.models;

import com.google.gson.annotations.SerializedName;

public class AcquirerData {

    @SerializedName("rrn")
    private String rrn;

    public void setRrn(String rrn){
        this.rrn = rrn;
    }

    public String getRrn(){
        return rrn;
    }
}