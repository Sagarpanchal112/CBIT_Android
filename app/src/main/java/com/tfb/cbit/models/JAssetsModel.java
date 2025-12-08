package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class JAssetsModel implements Serializable {
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
        @SerializedName("RedemedCC")
        @Expose
        public double RedemedCC;
        @SerializedName("AppliedCC")
        @Expose
        public double AppliedCC;

        public double getRedemedCC() {
            return RedemedCC;
        }

        public void setRedemedCC(double redemedCC) {
            RedemedCC = redemedCC;
        }

        public double getAppliedCC() {
            return AppliedCC;
        }

        public void setAppliedCC(double appliedCC) {
            AppliedCC = appliedCC;
        }

        public List<RedemedList> getRedemedLists() {
            return redemedLists;
        }

        public void setRedemedLists(List<RedemedList> redemedLists) {
            this.redemedLists = redemedLists;
        }


        @SerializedName("getAppliedReedeemedList")
        @Expose
        public List<RedemedList> redemedLists=null;


    }

    public class RedemedList implements Serializable{
        @SerializedName("id")
        @Expose
        public int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRedemedCC() {
            return RedemedCC;
        }

        public void setRedemedCC(int redemedCC) {
            RedemedCC = redemedCC;
        }

        public int getAppliedCC() {
            return AppliedCC;
        }

        public void setAppliedCC(int appliedCC) {
            AppliedCC = appliedCC;
        }

        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("RedemedCC")
        @Expose
        public int RedemedCC;
        @SerializedName("AppliedCC")
        @Expose
        public int AppliedCC;

    }

}
