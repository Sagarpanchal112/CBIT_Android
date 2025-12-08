package com.tfb.cbit.models.AddReddem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("wallate")
    @Expose
    private wallate wallate;

    public com.tfb.cbit.models.AddReddem.wallate getWallate() {
        return wallate;
    }

    public void setWallate(com.tfb.cbit.models.AddReddem.wallate wallate) {
        this.wallate = wallate;
    }
}
