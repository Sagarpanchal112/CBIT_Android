package com.tfb.cbit.models.wallet_transfer_otp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {
    @SerializedName("otpId")
    @Expose
    private int otpId;
    @SerializedName("otp")
    @Expose
    private int otp;

    public int getOtpId() {
        return otpId;
    }

    public void setOtpId(int otpId) {
        this.otpId = otpId;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

}
