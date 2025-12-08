package com.tfb.cbit.models.JticketFilter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JTicketName {
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("message")
    @Expose
    private String message;

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

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    @SerializedName("content")
    @Expose
    private Content content;


}
