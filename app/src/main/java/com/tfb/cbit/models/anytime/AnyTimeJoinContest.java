package com.tfb.cbit.models.anytime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tfb.cbit.models.join_contest.Content;

import java.io.Serializable;
import java.util.ArrayList;


public class AnyTimeJoinContest {

    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("content")
    @Expose
    private Content content;
    @SerializedName("message")
    @Expose
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<GameNumber> getGamenumber() {
        return gamenumber;
    }

    public void setGamenumber(ArrayList<GameNumber> gamenumber) {
        this.gamenumber = gamenumber;
    }

    @SerializedName("GameNumber")
    @Expose
    private ArrayList<GameNumber> gamenumber;



}

