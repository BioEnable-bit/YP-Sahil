package com.yespustak.yespustakapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * using this model for Room and retrofit
 */
@Entity(tableName = "users")
public class UserModel implements Serializable {

    //personal
    @PrimaryKey()
    @NonNull
    private String uid;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    private String password;
    private String confirmPassword;

    @SerializedName("dob")
    private String dob;

    @SerializedName("mobile_no")
    private String mobileNo;

    @SerializedName("watsapp_no")
    private String whatsappNo;

    @SerializedName("school_name")
    private String schoolName;

    @SerializedName("board_name")
    private String boardName;

    @SerializedName("standard_name")
    private String standardName;

    @SerializedName("section_name")
    private String sectionName;

    @SerializedName("gender")
    private String gender;

    @SerializedName("profile_photo")
    private String profilePhoto;

    @SerializedName("academic_year")
    private String academicYear;

    @SerializedName("roll_no")
    private String rollNo;

    @SerializedName("gr_number")
    private String grNumber;

    private String deviceId;

    //academics
    @SerializedName("board_id")
    private int boardId;

    @SerializedName("school_id")
    private int schoolId;

    @SerializedName("standard_id")
    private int standardId;

    public UserModel() {
        uid = "0";
    }

    @Ignore
    public UserModel(@NotNull String uid, String name, String email, String dob, String mobileNo, String whatsappNo, String schoolName, String boardName, String standardName, String sectionName, String deviceId) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.mobileNo = mobileNo;
        this.whatsappNo = whatsappNo;
        this.schoolName = schoolName;
        this.boardName = boardName;
        this.standardName = standardName;
        this.sectionName = sectionName;
        this.deviceId = deviceId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getWhatsappNo() {
        return whatsappNo;
    }

    public void setWhatsappNo(String whatsappNo) {
        this.whatsappNo = whatsappNo;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getStandardId() {
        return standardId;
    }

    public void setStandardId(int standardId) {
        this.standardId = standardId;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getGrNumber() {
        return grNumber;
    }

    public void setGrNumber(String grNumber) {
        this.grNumber = grNumber;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", dob='" + dob + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", whatsappNo='" + whatsappNo + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", boardName='" + boardName + '\'' +
                ", standardName='" + standardName + '\'' +
                ", sectionName='" + sectionName + '\'' +
                ", gender='" + gender + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", rollNo='" + rollNo + '\'' +
                ", grNumber='" + grNumber + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", boardId=" + boardId +
                ", schoolId=" + schoolId +
                ", standardId=" + standardId +
                '}';
    }
}
