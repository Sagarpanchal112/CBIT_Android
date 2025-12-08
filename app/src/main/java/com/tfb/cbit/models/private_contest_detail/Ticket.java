package com.tfb.cbit.models.private_contest_detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Ticket {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("totalJoin")
    @Expose
    private int totalJoin;
    @SerializedName("winners")
    @Expose
    private int winners;
    @SerializedName("users")
    @Expose
    private List<User> users = null;
    @SerializedName("isCancel")
    @Expose
    private boolean isCancel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getTotalJoin() {
        return totalJoin;
    }

    public void setTotalJoin(int totalJoin) {
        this.totalJoin = totalJoin;
    }

    public int getWinners() {
        return winners;
    }

    public void setWinners(int winners) {
        this.winners = winners;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public boolean isIsCancel() {
        return isCancel;
    }

    public void setIsCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }
}
