package com.yespustak.yespustakapp.models;

import com.google.gson.annotations.SerializedName;

public class BoardModel {

    @SerializedName("id")
    public int id;

    @SerializedName("board_name")
    String name;

    public BoardModel() {
    }

    public BoardModel(int id, String name) {
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
