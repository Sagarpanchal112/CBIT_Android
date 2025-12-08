package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReferalCriteriaChart implements Serializable {
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

    public Content getContents() {
        return content;
    }

    public void setContents(Content contents) {
        this.content = contents;
    }

    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("content")
    @Expose
    public Content content = null;

    public class Content {
        @SerializedName("ReferralList")
        @Expose
        public List<ReferralList> ReferralList = null;

        public List<ReferalCriteriaChart.ReferralList> getReferralList() {
            return ReferralList;
        }

        public void setReferralList(List<ReferalCriteriaChart.ReferralList> referralList) {
            ReferralList = referralList;
        }

        public String getUserCriteriaID() {
            return UserCriteriaID;
        }

        public void setUserCriteriaID(String userCriteriaID) {
            UserCriteriaID = userCriteriaID;
        }

        public int getUserRefferalNetwork() {
            return UserRefferalNetwork;
        }

        public void setUserRefferalNetwork(int userRefferalNetwork) {
            UserRefferalNetwork = userRefferalNetwork;
        }

        @SerializedName("UserCriteriaID")
        @Expose
        public String UserCriteriaID;
        @SerializedName("UserRefferalNetwork")
        @Expose
        public int UserRefferalNetwork;

        public String getUserReferalImage() {
            return UserReferalImage;
        }

        public void setUserReferalImage(String userReferalImage) {
            UserReferalImage = userReferalImage;
        }

        @SerializedName("UserReferalImage")
        @Expose
        public String UserReferalImage;

        public String getUserRefferalAllNetwork() {
            return UserRefferalAllNetwork;
        }

        public void setUserRefferalAllNetwork(String userRefferalAllNetwork) {
            UserRefferalAllNetwork = userRefferalAllNetwork;
        }

        @SerializedName("UserRefferalAllNetwork")
        @Expose
        public String UserRefferalAllNetwork;

    }
    public class ReferralList {
        @SerializedName("Level")
        @Expose
        public String Level;
        @SerializedName("Refferel")
        @Expose
        public int Refferel;

        public String getLevel() {
            return Level;
        }

        public void setLevel(String level) {
            Level = level;
        }

        public int getRefferel() {
            return Refferel;
        }

        public void setRefferel(int refferel) {
            Refferel = refferel;
        }

        public float getEM() {
            return EM;
        }

        public void setEM(float EM) {
            this.EM = EM;
        }

        public float getRefComm() {
            return RefComm;
        }

        public void setRefComm(float refComm) {
            RefComm = refComm;
        }

        public float getTDS() {
            return TDS;
        }

        public void setTDS(float TDS) {
            this.TDS = TDS;
        }

        @SerializedName("EM")
        @Expose
        public float EM;
        @SerializedName("RefComm")
        @Expose
        public float RefComm;
        @SerializedName("TDS")
        @Expose
        public float TDS;



    }

}
