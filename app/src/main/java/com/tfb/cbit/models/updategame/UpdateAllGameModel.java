package com.tfb.cbit.models.updategame;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateAllGameModel {

    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("content")
    @Expose
    private List<Content> content = null;
  /*  @SerializedName("content")
    @Expose
    private Content content;*/
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("isLockTime")
    @Expose
    private String isLockTime;


    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    @SerializedName("lockTime")
    @Expose
    private String lockTime;

    public String getIsLockTime() {
        return isLockTime;
    }

    public void setIsLockTime(String isLockTime) {
        this.isLockTime = isLockTime;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
