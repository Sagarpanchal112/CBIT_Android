package com.tfb.cbit.models.ReferralDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReferralDetailsModel {

    @SerializedName("statusCode")
    @Expose
    private int statusCode;

    @SerializedName("content")
    @Expose
    private Content content;

    @SerializedName("message")
    @Expose
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
