package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.PdfFormField;

import java.io.Serializable;
import java.util.List;

public class PdfData implements Serializable {

    @SerializedName("device_id")
    String deviceId;

    @SerializedName("book_id")
    int bookId;

    @SerializedName("fields")
    List<PdfFormField> fieldList;

    public PdfData(String deviceId, int bookId, List<PdfFormField> fieldList) {
        this.deviceId = deviceId;
        this.bookId = bookId;
        this.fieldList = fieldList;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public List<PdfFormField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<PdfFormField> fieldList) {
        this.fieldList = fieldList;
    }
}
