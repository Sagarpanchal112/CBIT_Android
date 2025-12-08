package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CheckPanStatus implements Serializable {
    @SerializedName("statusCode")
    @Expose
    public int statusCode;
    @SerializedName("message")
    @Expose
    public String message;

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
    public Content content;

    public class Content {

        @SerializedName("verify_pan")
        @Expose
        public int verify_pan;

        public int getVerify_pan() {
            return verify_pan;
        }

        public void setVerify_pan(int verify_pan) {
            this.verify_pan = verify_pan;
        }

        public ArrayList<UserWalletWithSckoet> getUserWalletWithSckoet() {
            return UserWalletWithSckoet;
        }

        public void setUserWalletWithSckoet(ArrayList<UserWalletWithSckoet> userWalletWithSckoet) {
            UserWalletWithSckoet = userWalletWithSckoet;
        }

        @SerializedName("UserWalletWithSckoet")
        @Expose
        public ArrayList<UserWalletWithSckoet> UserWalletWithSckoet;

    }

    public class UserWalletWithSckoet {
        @SerializedName("ccAmount")
        @Expose
        public double ccAmount;

        public double getCcAmount() {
            return ccAmount;
        }

        public void setCcAmount(double ccAmount) {
            this.ccAmount = ccAmount;
        }
    }


}
