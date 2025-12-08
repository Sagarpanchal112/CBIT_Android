package com.tfb.cbit.models.login_register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WallateDetails {
    @SerializedName("pbAmount")
    @Expose
    double pbAmount;
    @SerializedName("sbAmount")
    @Expose
    double sbAmount;
    @SerializedName("ccAmount")
    @Expose
    double ccAmount;
    @SerializedName("WalletAuth")
    @Expose
    String WalletAuth;

    public String getWalletAuth() {
        return WalletAuth;
    }

    public void setWalletAuth(String walletAuth) {
        WalletAuth = walletAuth;
    }

    public double getCcAmount() {
        return ccAmount;
    }

    public void setCcAmount(double ccAmount) {
        this.ccAmount = ccAmount;
    }

    public double getPbAmount() {
        return pbAmount;
    }

    public double getSbAmount() {
        return sbAmount;
    }
}
