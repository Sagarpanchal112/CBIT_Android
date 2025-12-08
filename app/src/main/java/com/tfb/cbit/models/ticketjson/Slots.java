package com.tfb.cbit.models.ticketjson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slots {
    @SerializedName("startValue")
    @Expose
    private String startValue;
    @SerializedName("endValue")
    @Expose
    private String endValue;
    @SerializedName("displayValue")
    @Expose
    private String displayValue;

    public Slots(String startValue,String endValue,String displayValue){
        this.startValue = startValue;
        this.endValue = endValue;
        this.displayValue = displayValue;
    }

    public String getStartValue() {
        return startValue;
    }

    public String getEndValue() {
        return endValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
