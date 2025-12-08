package com.tfb.cbit.models.contestdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserSelect {

    @SerializedName("startValue")
    @Expose
    private String startValue;

    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @SerializedName("endValue")
    @Expose
    private String endValue;
    @SerializedName("displayValue")
    @Expose
    private String displayValue;
    @SerializedName("selectValue")
    @Expose
    private String selectValue;

    public String getStartValue() {
        return startValue;
    }

    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getSelectValue() {
        return selectValue;
    }

    public void setSelectValue(String selectValue) {
        this.selectValue = selectValue;
    }
}
