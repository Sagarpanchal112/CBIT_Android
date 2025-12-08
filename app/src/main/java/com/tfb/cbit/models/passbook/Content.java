package com.tfb.cbit.models.passbook;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("type")
    @Expose
    private String type;

    public int getRedeemFlag() {
        return RedeemFlag;
    }

    public void setRedeemFlag(int redeemFlag) {
        RedeemFlag = redeemFlag;
    }

    @SerializedName("RedeemFlag")
    @Expose
    private int RedeemFlag;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("tds")
    @Expose
    private String tds;
    @SerializedName("beforebalance")
    @Expose
    private String beforebalance;

    public String getBeforebalance() {
        return beforebalance;
    }

    public void setBeforebalance(String beforebalance) {
        this.beforebalance = beforebalance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTds() {
        return tds;
    }

    public void setTds(String tds) {
        this.tds = tds;
    }
}
