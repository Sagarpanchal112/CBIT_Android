package com.tfb.cbit.models.gamelist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Content {
    @SerializedName("contest")
    @Expose
    private List<Contest> contest = null;
    @SerializedName("currentTime")
    @Expose
    private String currentTime;

    public List<Contest> getContest() {
        return contest;
    }

    public void setContest(List<Contest> contest) {
        this.contest = contest;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
