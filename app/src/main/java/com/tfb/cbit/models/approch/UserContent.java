package com.tfb.cbit.models.approch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserContent {
    @SerializedName("contest")
    @Expose
    private List<UserContest> contest = null;
    @SerializedName("currentTime")
    @Expose
    private String currentTime;

    @SerializedName("currentWaitingPeriod")
    @Expose
    private String currentWaitingPeriod;

    public String getCurrentWaitingPeriod() {
        return currentWaitingPeriod;
    }

    public void setCurrentWaitingPeriod(String currentWaitingPeriod) {
        this.currentWaitingPeriod = currentWaitingPeriod;
    }


    public List<UserContest> getContest() {
        return contest;
    }

    public void setContest(List<UserContest> contest) {
        this.contest = contest;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }


}
