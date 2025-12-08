package com.tfb.cbit.models.accounts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccountsModel {

    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("content")
    @Expose
    private List<Content> content = null;
    @SerializedName("message")
    @Expose
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
