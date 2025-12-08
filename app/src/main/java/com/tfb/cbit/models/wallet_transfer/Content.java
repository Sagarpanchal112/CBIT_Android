package com.tfb.cbit.models.wallet_transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("pbAmount")
    @Expose
    private double pbAmount;
    @SerializedName("sbAmount")
    @Expose
    private double sbAmount;
    @SerializedName("tbAmount")
    @Expose
    private double tbAmount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPbAmount() {
        return pbAmount;
    }

    public void setPbAmount(double pbAmount) {
        this.pbAmount = pbAmount;
    }

    public double getSbAmount() {
        return sbAmount;
    }

    public void setSbAmount(double sbAmount) {
        this.sbAmount = sbAmount;
    }

    public double getTbAmount() {
        return tbAmount;
    }

    public void setTbAmount(double tbAmount) {
        this.tbAmount = tbAmount;
    }
}
