package com.tfb.cbit.models.anytimegame;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tfb.cbit.models.anytimegame.Content;

import java.util.ArrayList;

public class AnyTimeGameResponse {
    public ArrayList<Content> getContent() {
        return content;
    }

    @SerializedName("statusCode")
    @Expose
    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setContent(ArrayList<Content> content) {
        this.content = content;
    }

    @SerializedName("content")
    @Expose
    private ArrayList<Content> content;
    @SerializedName("message")
    @Expose
    private String message;

}
