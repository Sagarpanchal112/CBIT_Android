package com.tfb.cbit.models.contestwinner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("startValue")
    @Expose
    private String startValue;
    @SerializedName("endValue")
    @Expose
    private String endValue;
   @SerializedName("winAmount")
    @Expose
    private String winAmount;
    @SerializedName("lockTime")
    @Expose
    private String lockTime;

    public String getReferral_image() {
        return referral_image;
    }

    public void setReferral_image(String referral_image) {
        this.referral_image = referral_image;
    }

    public String getItemImage() {
        return ItemImage;
    }

    public void setItemImage(String itemImage) {
        ItemImage = itemImage;
    }

    @SerializedName("referral_image")
    @Expose
    private String referral_image;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("contestStartTime")
    @Expose
    private String contestStartTime;
    @SerializedName("contestStartDate")
    @Expose
    private String contestStartDate;
    @SerializedName("profile_ItemImage")
    @Expose
    private String profileImage;
    @SerializedName("ItemImage")
    @Expose
    private String ItemImage;

    public String getUserCriteriaID() {
        return UserCriteriaID;
    }

    public void setUserCriteriaID(String userCriteriaID) {
        UserCriteriaID = userCriteriaID;
    }

    @SerializedName("UserCriteriaID")
    @Expose
    private String UserCriteriaID;

    public String getImage() {
        return ItemImage;
    }

    public void setImage(String ItemImage) {
        this.ItemImage = ItemImage;
    }

    @SerializedName("displayValue")
    @Expose
    private String displayValue;

    @SerializedName("winStatus")
    @Expose
    private String winStatus;

    public String getWinStatus() {
        return winStatus;
    }

    public void setWinStatus(String winStatus) {
        this.winStatus = winStatus;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getStartValue() {
        return startValue;
    }

    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    public String getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(String winAmount) {
        this.winAmount = winAmount;
    }

    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContestStartTime() {
        return contestStartTime;
    }

    public void setContestStartTime(String contestStartTime) {
        this.contestStartTime = contestStartTime;
    }

    public String getContestStartDate() {
        return contestStartDate;
    }

    public void setContestStartDate(String contestStartDate) {
        this.contestStartDate = contestStartDate;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
