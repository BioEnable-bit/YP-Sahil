package com.yespustak.yespustakapp.models;

import java.util.ArrayList;

public class VerticalModel {
    String title;
    ArrayList<BookModel> arrayList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<BookModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<BookModel> arrayList) {
        this.arrayList = arrayList;
    }
}
