package com.tfb.cbit.models.contest_pkg;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContestPrice {
    @SerializedName("contestID")
    @Expose
    private int contestID = 0;

    @SerializedName("contestPriceID")
    @Expose
    private int contestPriceID = 0;

    @SerializedName("id")
    @Expose
    private int id = 0;

    @SerializedName("contestId")
    @Expose
    private int contestId = 0;
    @SerializedName("amount")
    @Expose
    private int amount = 0;

    @SerializedName("maxWinner")
    @Expose
    private int maxWinner = 0;

    @SerializedName("minJoin")
    @Expose
    private int minJoin = 0;

    @SerializedName("bracketSize")
    @Expose
    private int bracketSize = 0;


    @SerializedName("commision")
    @Expose
    private int commision = 0;

    @SerializedName("MaxWinJTicket")
    @Expose
    private int MaxWinJTicket = 0;

    @SerializedName("JTicketCommision")
    @Expose
    private int JTicketCommision = 0;

    @SerializedName("jTicketID")
    @Expose
    private int jTicketID = 0;

    @SerializedName("status")
    @Expose
    private String status = "";

    @SerializedName("created_at")
    @Expose
    private String created_at = "";
    private boolean isSelected = false;

    public int getContestID() {
        return contestID;
    }

    public void setContestID(int contestID) {
        this.contestID = contestID;
    }

    public int getContestPriceID() {
        return contestPriceID;
    }

    public void setContestPriceID(int contestPriceID) {
        this.contestPriceID = contestPriceID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getMaxWinner() {
        return maxWinner;
    }

    public void setMaxWinner(int maxWinner) {
        this.maxWinner = maxWinner;
    }

    public int getMinJoin() {
        return minJoin;
    }

    public void setMinJoin(int minJoin) {
        this.minJoin = minJoin;
    }

    public int getBracketSize() {
        return bracketSize;
    }

    public void setBracketSize(int bracketSize) {
        this.bracketSize = bracketSize;
    }

    public int getCommision() {
        return commision;
    }

    public void setCommision(int commision) {
        this.commision = commision;
    }

    public int getMaxWinJTicket() {
        return MaxWinJTicket;
    }

    public void setMaxWinJTicket(int maxWinJTicket) {
        MaxWinJTicket = maxWinJTicket;
    }

    public int getJTicketCommision() {
        return JTicketCommision;
    }

    public void setJTicketCommision(int JTicketCommision) {
        this.JTicketCommision = JTicketCommision;
    }

    public int getjTicketID() {
        return jTicketID;
    }

    public void setjTicketID(int jTicketID) {
        this.jTicketID = jTicketID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
