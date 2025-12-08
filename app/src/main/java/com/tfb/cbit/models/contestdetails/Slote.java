package com.tfb.cbit.models.contestdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slote {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("startValue")
    @Expose
    private int startValue;
    @SerializedName("endValue")
    @Expose
    private int endValue;

    public int getRow_index() {
        return row_index;
    }

    public void setRow_index(int row_index) {
        this.row_index = row_index;
    }

    @SerializedName("row_index")
    @Expose
    private int row_index;
    @SerializedName("displayValue")
    @Expose
    private String displayValue;
    @SerializedName("isSelected")
    @Expose
    private boolean isSelected;

    @SerializedName("objectID")
    @Expose
    private int objectID;
    @SerializedName("Image")
    @Expose
    private String Image;

    public int getObjectID() {
        return objectID;
    }

    public void setObjectID(int objectID) {
        this.objectID = objectID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    @SerializedName("selectValue")
    @Expose
    private String selectValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartValue() {
        return startValue;
    }

    public void setStartValue(int startValue) {
        this.startValue = startValue;
    }

    public int getEndValue() {
        return endValue;
    }

    public void setEndValue(int endValue) {
        this.endValue = endValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getSelectValue() {
        return selectValue;
    }

    public void setSelectValue(String selectValue) {
        this.selectValue = selectValue;
    }
}
