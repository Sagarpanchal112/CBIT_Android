package com.tfb.cbit.models.user_join_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tfb.cbit.models.private_contest_detail.User;

import java.util.List;

public class UserJoinModel {

    @SerializedName("statusCode")
    @Expose
    private int statusCode;

    @SerializedName("content")
    @Expose
    private List<User> content = null;

    @SerializedName("message")
    @Expose
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<User> getContent() {
        return content;
    }

    public void setContent(List<User> content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
