package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReferalCriteria implements Serializable {
    @SerializedName("statusCode")
    @Expose
    public int statusCode;

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


    @SerializedName("message")
    @Expose
    public String message;

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    @SerializedName("content")
    @Expose
    public List<Content> content = null;

    public class Content {
        @SerializedName("TotalReferal")
        @Expose
        public int TotalReferal;

        public int getTotalReferal() {
            return TotalReferal;
        }

        public void setTotalReferal(int totalReferal) {
            TotalReferal = totalReferal;
        }

        public String getCommissionPct() {
            return CommissionPct;
        }

        public void setCommissionPct(String commissionPct) {
            CommissionPct = commissionPct;
        }

        public int getCommissionLevel() {
            return CommissionLevel;
        }

        public void setCommissionLevel(int commissionLevel) {
            CommissionLevel = commissionLevel;
        }

        public String getReferalLevelName() {
            return ReferalLevelName;
        }

        public void setReferalLevelName(String referalLevelName) {
            ReferalLevelName = referalLevelName;
        }

        public int getReferalID() {
            return ReferalID;
        }

        public void setReferalID(int referalID) {
            ReferalID = referalID;
        }

        @SerializedName("CommissionPct")
        @Expose
        public String CommissionPct;

        @SerializedName("CommissionLevel")
        @Expose
        public int CommissionLevel;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        @SerializedName("ReferalLevelName")
        @Expose
        public String ReferalLevelName;


        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("ReferalID")
        @Expose
        public int ReferalID;


    }

}
