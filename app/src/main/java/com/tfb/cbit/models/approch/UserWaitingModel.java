package com.tfb.cbit.models.approch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserWaitingModel {
    @SerializedName("statusCode")
    @Expose
    private int statusCode;


    @SerializedName("content")
    @Expose
    private UserContent content;

    @SerializedName("message")
    @Expose
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public UserContent getContent() {
        return content;
    }

    public void setContent(UserContent content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
