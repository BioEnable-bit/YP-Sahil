package com.yespustak.yespustakapp.models;

import com.pspdfkit.forms.FormType;

import java.io.Serializable;

public class PdfFormField implements Serializable {

    private FormType type;
    private String key;
    private String value;

    public PdfFormField() {
    }

    public PdfFormField(FormType type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public FormType getType() {
        return type;
    }

    public void setType(FormType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Field{" +
                "type=" + type +
                ", name='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
