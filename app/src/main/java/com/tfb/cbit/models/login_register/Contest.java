package com.tfb.cbit.models.login_register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contest {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("middelName")
    @Expose
    private String middelName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("ReferralCode")
    @Expose
    private String ReferralCode;

    public String getImageDownload() {
        return ImageDownload;
    }

    public void setImageDownload(String imageDownload) {
        ImageDownload = imageDownload;
    }

    @SerializedName("ImageDownload")
    @Expose
    private String ImageDownload;

    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("myCode")
    @Expose
    private String myCode;
    @SerializedName("setNotification")
    @Expose
    private int setNotification;
    @SerializedName("wallateDetails")
    @Expose
    private WallateDetails wallateDetails;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("verify_pan")
    @Expose
    private int verify_pan;
    @SerializedName("verify_bank")
    @Expose
    private int verify_bank;
    @SerializedName("verify_email")
    @Expose
    private int verify_email;
    @SerializedName("AutoPilot")
    @Expose
    private int AutoPilot;
    @SerializedName("isRedeem")
    @Expose
    private int isRedeem;

    public int getAutoPilot() {
        return AutoPilot;
    }

    public void setAutoPilot(int autoPilot) {
        AutoPilot = autoPilot;
    }

    public int getIsRedeem() {
        return isRedeem;
    }

    public void setIsRedeem(int isRedeem) {
        this.isRedeem = isRedeem;
    }

    public String getReferralCode() {
        return ReferralCode;
    }

    public void setReferralCode(String referralCode) {
        ReferralCode = referralCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getMyCode() {
        return myCode;
    }

    public void setMyCode(String myCode) {
        this.myCode = myCode;
    }

    public int getSetNotification() {
        return setNotification;
    }

    public void setSetNotification(int setNotification) {
        this.setNotification = setNotification;
    }

    public WallateDetails getWallateDetails() {
        return wallateDetails;
    }

    public String getUserName() {
        return userName;
    }

    public int getVerify_bank() {
        return verify_bank;
    }

    public int getVerify_pan() {
        return verify_pan;
    }

    public int getVerify_email() {
        return verify_email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddelName() {
        return middelName;
    }

    public String getLastName() {
        return lastName;
    }
}
