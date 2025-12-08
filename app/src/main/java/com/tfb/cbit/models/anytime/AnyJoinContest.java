package com.tfb.cbit.models.anytime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tfb.cbit.models.join_contest.Content;

import java.util.ArrayList;


public class AnyJoinContest {

    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("content")
    @Expose
    private Content content;
    @SerializedName("message")
    @Expose
    private String message;

    public ArrayList<String> getGameNumber() {
        return GameNumber;
    }

    public void setGameNumber(ArrayList<String> gameNumber) {
        GameNumber = gameNumber;
    }

    @SerializedName("GameNumber")
    @Expose
    private ArrayList<String> GameNumber;

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
}
