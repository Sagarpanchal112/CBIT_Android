package com.tfb.cbit.models;

import java.io.Serializable;

public class SelectedImage implements Serializable {
    public int categoryId;
    public int itemId;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
