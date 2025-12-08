package com.tfb.cbit.models.RedeemJTicket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contest {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("price")
    @Expose
    private float price;
    @SerializedName("redenption_from")
    @Expose
    private float redenptionfrom;
    @SerializedName("redenption_to")
    @Expose
    private float redenptionto;
    @SerializedName("waiting")
    @Expose
    private int waiting;

    @SerializedName("applyCount")
    @Expose
    private int applyCount;

    public int getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(int applyCount) {
        this.applyCount = applyCount;
    }

    public float getRedenptionfrom() {
        return redenptionfrom;
    }

    public void setRedenptionfrom(float redenptionfrom) {
        this.redenptionfrom = redenptionfrom;
    }

    public float getRedenptionto() {
        return redenptionto;
    }

    public void setRedenptionto(float redenptionto) {
        this.redenptionto = redenptionto;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getWaiting() {
        return waiting;
    }

    public void setWaiting(int waiting) {
        this.waiting = waiting;
    }
}
