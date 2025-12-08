package com.tfb.cbit.models.contestdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoxJson {

    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("symbol")
    @Expose
    private String symbol;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    @SerializedName("Image")
    @Expose
    private String Image;
    @SerializedName("color")
    @Expose
    private String color;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
