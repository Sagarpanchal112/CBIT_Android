package com.tfb.cbit.models.anytimegame;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SlotesValue {

    @SerializedName("displayValue")
    @Expose
    private String displayValue;

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
}
