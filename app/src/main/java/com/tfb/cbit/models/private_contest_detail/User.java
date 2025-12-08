package com.tfb.cbit.models.private_contest_detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;

    public String getReferral_image() {
        return referral_image;
    }

    public void setReferral_image(String referral_image) {
        this.referral_image = referral_image;
    }

    @SerializedName("referral_image")
    @Expose
    private String referral_image;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
