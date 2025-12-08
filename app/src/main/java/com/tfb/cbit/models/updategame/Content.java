package com.tfb.cbit.models.updategame;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("contestPriceId")
    @Expose
    private int contestPriceId;

    @SerializedName("isLock")
    @Expose
    private boolean isLock;

    @SerializedName("isLockTime")
    @Expose
    private String isLockTime;

    @SerializedName("position")
    @Expose
    private int position;

    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    @SerializedName("lockTime")
    @Expose
    private String lockTime;

    @SerializedName("startValue")
    @Expose
    private int startValue;

    @SerializedName("endValue")
    @Expose
    private int endValue;

    @SerializedName("displayValue")
    @Expose
    private String displayValue;

    public int getContestPriceId() {
        return contestPriceId;
    }

    public void setContestPriceId(int contestPriceId) {
        this.contestPriceId = contestPriceId;
    }

    public boolean isIsLock() {
        return isLock;
    }

    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }

    public String getIsLockTime() {
        return isLockTime;
    }

    public void setIsLockTime(String isLockTime) {
        this.isLockTime = isLockTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getStartValue() {
        return startValue;
    }

    public void setStartValue(int startValue) {
        this.startValue = startValue;
    }

    public int getEndValue() {
        return endValue;
    }

    public void setEndValue(int endValue) {
        this.endValue = endValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
