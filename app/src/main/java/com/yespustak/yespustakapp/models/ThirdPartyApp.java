package com.yespustak.yespustakapp.models;

import com.google.gson.annotations.SerializedName;

public class ThirdPartyApp {

    @SerializedName("app_name")
    private String name;

    @SerializedName("package_name")
    private String packageName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
