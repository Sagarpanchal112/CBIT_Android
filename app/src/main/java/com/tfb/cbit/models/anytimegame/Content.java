package com.tfb.cbit.models.anytimegame;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {
    @SerializedName("contestID")
    @Expose
    private int contestID;

    @SerializedName("slotes")
    @Expose
    public String slotes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("name")
    @Expose
    public String name;

    public String getSlotes() {
        return slotes;
    }

    public void setSlotes(String slotes) {
        this.slotes = slotes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected = false;

    public int getContestID() {
        return contestID;
    }

    public void setContestID(int contestID) {
        this.contestID = contestID;
    }

    public int getContestpriceID() {
        return contestpriceID;
    }

    public void setContestpriceID(int contestpriceID) {
        this.contestpriceID = contestpriceID;
    }

    public String getGame_type() {
        return game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
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

    public int getMaxJoin() {
        return maxJoin;
    }

    public void setMaxJoin(int maxJoin) {
        this.maxJoin = maxJoin;
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

    public int getNo_of_players() {
        return no_of_players;
    }

    public void setNo_of_players(int no_of_players) {
        this.no_of_players = no_of_players;
    }

    public int getGame_played() {
        return game_played;
    }

    public void setGame_played(int game_played) {
        this.game_played = game_played;
    }

    public int getPlayers_played() {
        return players_played;
    }

    public void setPlayers_played(int players_played) {
        this.players_played = players_played;
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

    public int getPendingTickets() {
        return pendingTickets;
    }

    public void setPendingTickets(int pendingTickets) {
        this.pendingTickets = pendingTickets;
    }

    @SerializedName("contestpriceID")
    @Expose
    private int contestpriceID;

    public String getWinningAmount() {
        return winningAmount;
    }

    public void setWinningAmount(String winningAmount) {
        this.winningAmount = winningAmount;
    }

    public String getNo_of_winners() {
        return no_of_winners;
    }

    public void setNo_of_winners(String no_of_winners) {
        this.no_of_winners = no_of_winners;
    }

    @SerializedName("winningAmount")
    @Expose
    private String winningAmount;
    @SerializedName("no_of_winners")
    @Expose
    private String no_of_winners;
    @SerializedName("game_type")
    @Expose
    private String game_type;
    @SerializedName("gameMode")
    @Expose
    private String gameMode;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("contestId")
    @Expose
    private int contestId;
    @SerializedName("amount")
    @Expose
    private int amount;
    @SerializedName("maxWinner")
    @Expose
    private int maxWinner;
    @SerializedName("minJoin")
    @Expose
    private int minJoin;
    @SerializedName("maxJoin")
    @Expose
    private int maxJoin;
    @SerializedName("bracketSize")
    @Expose
    private int bracketSize;
    @SerializedName("commision")
    @Expose
    private int commision;
    @SerializedName("MaxWinJTicket")
    @Expose
    private int MaxWinJTicket;
    @SerializedName("JTicketCommision")
    @Expose
    private int JTicketCommision;
    @SerializedName("jTicketID")
    @Expose
    private int jTicketID;
    @SerializedName("no_of_players")
    @Expose
    private int no_of_players;
    @SerializedName("game_played")
    @Expose
    private int game_played;
    @SerializedName("players_played")
    @Expose
    private int players_played;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("pendingTickets")
    @Expose
    private int pendingTickets;
}
