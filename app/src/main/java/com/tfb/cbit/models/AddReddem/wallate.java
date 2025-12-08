package com.tfb.cbit.models.AddReddem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class wallate {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("ccAmount")
    @Expose
    private String ccAmount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCcAmount() {
        return ccAmount;
    }

    public void setCcAmount(String ccAmount) {
        this.ccAmount = ccAmount;
    }
}
