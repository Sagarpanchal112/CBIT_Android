package com.tfb.cbit.models.notification.setnotification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {
    @SerializedName("setNotification")
    @Expose
    private int setNotification;

    public int getSetNotification() {
        return setNotification;
    }

    public void setSetNotification(int setNotification) {
        this.setNotification = setNotification;
    }
}
