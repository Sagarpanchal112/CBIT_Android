package com.tfb.cbit.models.JticketFilter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Names {
    @SerializedName("id")
    @Expose
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("name")
    @Expose
    private String name;

}
