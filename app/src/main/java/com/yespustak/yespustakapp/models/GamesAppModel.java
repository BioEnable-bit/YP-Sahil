package com.yespustak.yespustakapp.models;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

public class GamesAppModel {

    private Drawable icon;

    @SerializedName("app_name")
    private String name;

    @SerializedName("package_name")
    private String packageName;

    public GamesAppModel(String name, String packages, Drawable icon) {
        this.name = name;
        this.icon = icon;
        this.packageName = packages;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }
}
