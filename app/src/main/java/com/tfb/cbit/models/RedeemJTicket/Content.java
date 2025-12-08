package com.tfb.cbit.models.RedeemJTicket;

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
    @SerializedName("TotalEntry")
    @Expose
    private String TotalEntry;
    @SerializedName("TotalEarning")
    @Expose
    private String TotalEarning;

    public String getTotalEntry() {
        return TotalEntry;
    }

    public void setTotalEntry(String totalEntry) {
        TotalEntry = totalEntry;
    }

    public String getTotalEarning() {
        return TotalEarning;
    }

    public void setTotalEarning(String totalEarning) {
        TotalEarning = totalEarning;
    }

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
