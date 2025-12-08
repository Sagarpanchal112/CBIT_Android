package com.tfb.cbit.models;

import java.io.Serializable;

public class SendSpinnerItem implements Serializable {
    public String id="";
    public String Item="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String Image="";

}
