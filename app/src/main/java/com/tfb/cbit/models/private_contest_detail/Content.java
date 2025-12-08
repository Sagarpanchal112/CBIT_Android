package com.tfb.cbit.models.private_contest_detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tfb.cbit.models.contestdetails.BoxJson;

import java.util.List;

public class Content {

    @SerializedName("level")
    @Expose
    private int level;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("boxJson")
    @Expose
    private List<BoxJson> boxJson = null;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("gameStatus")
    @Expose
    private String gameStatus;
    @SerializedName("ticket")
    @Expose
    private List<Ticket> ticket = null;
    @SerializedName("totalAmount")
    @Expose
    private String totalAmount;
    @SerializedName("totalCommision")
    @Expose
    private String totalCommision;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<BoxJson> getBoxJson() {
        return boxJson;
    }

    public void setBoxJson(List<BoxJson> boxJson) {
        this.boxJson = boxJson;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public List<Ticket> getTicket() {
        return ticket;
    }

    public void setTicket(List<Ticket> ticket) {
        this.ticket = ticket;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalCommision() {
        return totalCommision;
    }

    public void setTotalCommision(String totalCommision) {
        this.totalCommision = totalCommision;
    }
}
