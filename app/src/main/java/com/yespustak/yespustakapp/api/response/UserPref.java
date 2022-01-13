package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.api.custom.BooleanDeserializer;

public class UserPref {

    int id;
    String uid;

    @SerializedName("personal_details_flag")
    @JsonAdapter(BooleanDeserializer.class) //convert 1 and 0 to true false
    boolean personalDetails;

    @SerializedName("otp_verification_flag")
    @JsonAdapter(BooleanDeserializer.class)
    boolean otpVerification;

    @SerializedName("academic_details_flag")
    @JsonAdapter(BooleanDeserializer.class)
    boolean academicDetails;


    public int getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public boolean isPersonalDetails() {
        return personalDetails;
    }

    public boolean isOtpVerification() {
        return otpVerification;
    }

    public boolean isAcademicDetails() {
        return academicDetails;
    }
}
