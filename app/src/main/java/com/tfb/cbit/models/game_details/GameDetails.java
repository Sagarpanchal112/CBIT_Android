package com.tfb.cbit.models.game_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GameDetails {

    @SerializedName("isGameStart")
    @Expose
    private int isGameStart;
    @SerializedName("box_json")
    @Expose
    private List<BoxJson> boxJson = null;
    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("answer")
    @Expose
    private int answer;
    @SerializedName("level")
    @Expose
    private int level;
    @SerializedName("bracket_size")
    @Expose
    private int bracketSize;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("ansRangeMin")
    @Expose
    private int ansRangeMin;
    @SerializedName("ansRangeMax")
    @Expose
    private int ansRangeMax;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("currentTime")
    @Expose
    private String currentTime;
    @SerializedName("purchaseTickets")
    @Expose
    private PurchaseTickets purchaseTickets;
    @SerializedName("joinContest")
    @Expose
    private int joinContest;
    @SerializedName("maxJoin")
    @Expose
    private int maxJoin;

    public int getIsGameStart() {
        return isGameStart;
    }

    public void setIsGameStart(int isGameStart) {
        this.isGameStart = isGameStart;
    }

    public List<BoxJson> getBoxJson() {
        return boxJson;
    }

    public void setBoxJson(List<BoxJson> boxJson) {
        this.boxJson = boxJson;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getBracketSize() {
        return bracketSize;
    }

    public void setBracketSize(int bracketSize) {
        this.bracketSize = bracketSize;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAnsRangeMin() {
        return ansRangeMin;
    }

    public void setAnsRangeMin(int ansRangeMin) {
        this.ansRangeMin = ansRangeMin;
    }

    public int getAnsRangeMax() {
        return ansRangeMax;
    }

    public void setAnsRangeMax(int ansRangeMax) {
        this.ansRangeMax = ansRangeMax;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public PurchaseTickets getPurchaseTickets() {
        return purchaseTickets;
    }

    public void setPurchaseTickets(PurchaseTickets purchaseTickets) {
        this.purchaseTickets = purchaseTickets;
    }

    public int getJoinContest() {
        return joinContest;
    }

    public void setJoinContest(int joinContest) {
        this.joinContest = joinContest;
    }

    public int getMaxJoin() {
        return maxJoin;
    }

    public void setMaxJoin(int maxJoin) {
        this.maxJoin = maxJoin;
    }
}
