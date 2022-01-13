package com.yespustak.yespustakapp.models;

import com.google.gson.annotations.SerializedName;

public class SchoolModel {

    @SerializedName("id")
    public int id;

    @SerializedName("school_name")
    String name;

    @SerializedName("address")
    String address;

    @SerializedName("city")
    String city;

    @SerializedName("pincode")
    int pincode;

    @SerializedName("board_id")
    int boardId;

    public SchoolModel() {
    }

    public SchoolModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
