package com.tfb.cbit.models.contestdetails;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Ticket {
    public static transient Ticket dNode = null;
    @SerializedName("contestPriceId")
    @Expose
    private int contestPriceId;
    @SerializedName("amount")
    @Expose
    private int amount;
    @SerializedName("totalJTicketHolder")
    @Expose
    private String totalJTicketHolder;
    @SerializedName("perJTicket")
    @Expose
    private double perJTicket;
    @SerializedName("totalWinnings")
    @Expose
    private double totalWinnings;

    public int getMinJoin() {
        return minJoin;
    }

    public void setMinJoin(int minJoin) {
        this.minJoin = minJoin;
    }

    @SerializedName("minJoin")
    @Expose
    private int minJoin;
    @SerializedName("maxWinners")
    @Expose
    private int maxWinners;

    @SerializedName("no_of_players")
    @Expose
    private int no_of_players;

    public int getNo_of_players() {
        return no_of_players;
    }

    public void setNo_of_players(int no_of_players) {
        this.no_of_players = no_of_players;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    @SerializedName("played")
    @Expose
    private int played;
    @SerializedName("pending")
    @Expose
    private int pending;
    @SerializedName("maxWinnersPrc")
    @Expose
    private String maxWinnersPrc;
    @SerializedName("isPurchased")
    @Expose
    private int isPurchased;
    @SerializedName("totalTickets")
    @Expose
    private int totalTickets;

    public int getContestUSerID() {
        return contestUSerID;
    }

    public void setContestUSerID(int contestUSerID) {
        this.contestUSerID = contestUSerID;
    }

    @SerializedName("game_no")
    @Expose
    private  int game_no;
    @SerializedName("contestUSerID")
    @Expose
    private  int contestUSerID;

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    @SerializedName("contestId")
    @Expose
    private int contestId;
    @SerializedName("isLock")
    @Expose
    private boolean isLock;

    public int getGame_no() {
        return game_no;
    }

    public void setGame_no(int game_no) {
        this.game_no = game_no;
    }

    @SerializedName("lockTime")
    @Expose
    private String lockTime;

    public String getNowin() {
        return nowin;
    }

    public void setNowin(String nowin) {
        this.nowin = nowin;
    }

    @SerializedName("nowin")
    @Expose
    private String nowin;
    @SerializedName("win")
    @Expose
    private boolean win;
    @SerializedName("isCancel")
    @Expose
    private boolean isCancel;

    public String getTotalCCWinAmount() {
        return totalCCWinAmount;
    }

    public void setTotalCCWinAmount(String totalCCWinAmount) {
        this.totalCCWinAmount = totalCCWinAmount;
    }

    @SerializedName("winAmount")
    @Expose
    private String winAmount;
    @SerializedName("totalCCWinAmount")
    @Expose
    private String totalCCWinAmount;
    @SerializedName("slotes")
    @Expose
    private List<Slote> slotes = null;
    @SerializedName("bracketSize")
    @Expose
    private int bracketSize;
    @SerializedName("user_select")
    @Expose
    private UserSelect userSelect;

    private boolean isSelected = false;

    public String getImageView() {
        return imageView;
    }

    public void setImageView(String imageView) {
        this.imageView = imageView;
    }

    private String minValue="",maxValue="";
    private String displayView = "",imageView="";

    public String getTotalJTicketHolder() {
        return totalJTicketHolder;
    }

    public void setTotalJTicketHolder(String totalJTicketHolder) {
        this.totalJTicketHolder = totalJTicketHolder;
    }

    public double getPerJTicket() {
        return perJTicket;
    }

    public void setPerJTicket(double perJTicket) {
        this.perJTicket = perJTicket;
    }

    public int getContestPriceId() {
        return contestPriceId;
    }

    public void setContestPriceId(int contestPriceId) {
        this.contestPriceId = contestPriceId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getTotalWinnings() {
        return totalWinnings;
    }

    public void setTotalWinnings(int totalWinnings) {
        this.totalWinnings = totalWinnings;
    }

    public int getMaxWinners() {
        return maxWinners;
    }

    public void setMaxWinners(int maxWinners) {
        this.maxWinners = maxWinners;
    }

    public int getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(int isPurchased) {
        this.isPurchased = isPurchased;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public boolean isIsLock() {
        return isLock;
    }

    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }

    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public List<Slote> getSlotes() {
        return slotes;
    }

    public void setSlotes(List<Slote> slotes) {
        this.slotes = slotes;
    }

    public int getBracketSize() {
        return bracketSize;
    }

    public void setBracketSize(int bracketSize) {
        this.bracketSize = bracketSize;
    }

    public UserSelect getUserSelect() {
        return userSelect;
    }

    public void setUserSelect(UserSelect userSelect) {
        this.userSelect = userSelect;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getWinAmount() {
        return winAmount;
    }

    public void setDisplayView(String displayView) {
        this.displayView = displayView;
    }

    public String getDisplayView() {
        return displayView;
    }

    public String getMaxWinnersPrc() {
        return maxWinnersPrc;
    }


}
