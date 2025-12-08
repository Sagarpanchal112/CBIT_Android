package com.tfb.cbit.models.login_register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class LoginRegisterModel {
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("contest")
    @Expose
    private Contest contest;
    @SerializedName("message")
    @Expose
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
