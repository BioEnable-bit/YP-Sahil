package com.yespustak.yespustakapp.api.response;

public class RazorpayPaymentError {

    String code;
    String description;
    String source;
    String step;
    String reason;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public String getStep() {
        return step;
    }

    public String getReason() {
        return reason;
    }
}
