package com.tfb.cbit.models.redeem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("sbAmount")
    @Expose
    private double sbAmount;
    @SerializedName("pbAmount")
    @Expose
    private double pbAmount;
    @SerializedName("tbAmount")
    @Expose
    private double tbAmount;

    public double getSbAmount() {
        return sbAmount;
    }

    public void setSbAmount(double sbAmount) {
        this.sbAmount = sbAmount;
    }

    public double getPbAmount() {
        return pbAmount;
    }

    public void setPbAmount(double pbAmount) {
        this.pbAmount = pbAmount;
    }

    public double getTbAmount() {
        return tbAmount;
    }

    public void setTbAmount(double tbAmount) {
        this.tbAmount = tbAmount;
    }
}
