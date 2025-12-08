package com.tfb.cbit.models.join_contest;

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

    public double getPbAmount() {
        return pbAmount;
    }

    public double getSbAmount() {
        return sbAmount;
    }

    public double getTbAmount() {
        return tbAmount;
    }
}
