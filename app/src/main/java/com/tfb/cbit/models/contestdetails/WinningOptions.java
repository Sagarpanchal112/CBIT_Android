package com.tfb.cbit.models.contestdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WinningOptions {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("ImageUrl")
    @Expose
    private String ImageUrl;
    @SerializedName("count")
    @Expose
    private int count;

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @SerializedName("objectNo")
    @Expose
    private int objectNo;

    @SerializedName("Item")
    @Expose
    private String Item;
    @SerializedName("Image")
    @Expose
    private String Image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObjectNo() {
        return objectNo;
    }

    public void setObjectNo(int objectNo) {
        this.objectNo = objectNo;
    }

    public String getItem() {
        return Item;
    }

    public void setItem(String item) {
        Item = item;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}

