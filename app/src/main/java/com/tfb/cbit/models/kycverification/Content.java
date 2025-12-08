package com.tfb.cbit.models.kycverification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("pan_image")
    @Expose
    private String panImage;
    @SerializedName("pan_name")
    @Expose
    private String panName;
    @SerializedName("pan_number")
    @Expose
    private String panNumber;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("verify_pan")
    @Expose
    private int verifyPan;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public String getPanImage() {
        return panImage;
    }

    public void setPanImage(String panImage) {
        this.panImage = panImage;
    }

    public String getPanName() {
        return panName;
    }

    public void setPanName(String panName) {
        this.panName = panName;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getVerifyPan() {
        return verifyPan;
    }

    public void setVerifyPan(int verifyPan) {
        this.verifyPan = verifyPan;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
