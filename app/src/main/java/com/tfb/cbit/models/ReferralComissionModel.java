package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReferralComissionModel implements Serializable {
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

        public String getEarning() {
            return Earning;
        }

        public void setEarning(String earning) {
            Earning = earning;
        }

        public String getTds() {
            return tds;
        }

        public void setTds(String tds) {
            this.tds = tds;
        }

        @SerializedName("Earning")
        @Expose
        public String Earning;

        public String getReferralCount() {
            return ReferralCount;
        }

        public void setReferralCount(String referralCount) {
            ReferralCount = referralCount;
        }

        @SerializedName("tds")
        @Expose
        public String tds;


        @SerializedName("ReferralCount")
        @Expose
        public String ReferralCount;

       }


}
