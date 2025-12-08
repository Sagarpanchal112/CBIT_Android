package com.tfb.cbit.models.MyJTicket;

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
    @SerializedName("ADP")
    @Expose
    private String ADP;

    public String getBAP() {
        return BAP;
    }

    public void setBAP(String BAP) {
        this.BAP = BAP;
    }

    public String getTAP() {
        return TAP;
    }

    public void setTAP(String TAP) {
        this.TAP = TAP;
    }

    @SerializedName("BAP")
    @Expose
    private String BAP;
    @SerializedName("TAP")
    @Expose
    private String TAP;
    @SerializedName("DayOfJoin")
    @Expose
    private String DayOfJoin;

    public String getDayOfJoin() {
        return DayOfJoin;
    }

    public void setDayOfJoin(String dayOfJoin) {
        DayOfJoin = dayOfJoin;
    }

    public String getADP() {
        return ADP;
    }

    public void setADP(String ADP) {
        this.ADP = ADP;
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
