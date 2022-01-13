package com.yespustak.yespustakapp.models;

public class TeachersModel {

    private String id;
    private String name;
    private String create_at;
    private String update_at;

    public TeachersModel(String id, String name, String create_at, String update_at) {
        this.id = id;
        this.name = name;
        this.create_at = create_at;
        this.update_at = update_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }
}
