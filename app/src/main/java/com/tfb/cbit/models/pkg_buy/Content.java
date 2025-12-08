package com.tfb.cbit.models.pkg_buy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("sbAmount")
    @Expose
    private String sbAmount;
    @SerializedName("pbAmount")
    @Expose
    private String pbAmount;
    @SerializedName("tbAmount")
    @Expose
    private String tbAmount;

    public String getSbAmount() {
        return sbAmount;
    }

    public void setSbAmount(String sbAmount) {
        this.sbAmount = sbAmount;
    }

    public String getPbAmount() {
        return pbAmount;
    }

    public void setPbAmount(String pbAmount) {
        this.pbAmount = pbAmount;
    }

    public String getTbAmount() {
        return tbAmount;
    }

    public void setTbAmount(String tbAmount) {
        this.tbAmount = tbAmount;
    }
}
