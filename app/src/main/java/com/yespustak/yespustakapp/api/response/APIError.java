package com.yespustak.yespustakapp.api.response;

public class APIError {

    private int statusCode;
    private String message;

    public APIError() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message + "\n";
    }
}
