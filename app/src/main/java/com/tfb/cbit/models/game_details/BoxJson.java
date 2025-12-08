package com.tfb.cbit.models.game_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoxJson {
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("sign")
    @Expose
    private String sign;
    @SerializedName("value")
    @Expose
    private int value;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
