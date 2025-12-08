package com.tfb.cbit.models.contest_pkg;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tfb.cbit.models.contestdetails.UserSelect;

import java.util.ArrayList;
import java.util.List;

public class Content {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("amount")
    @Expose
    private int amount;
    @SerializedName("commission")
    @Expose
    private String commission;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("validity")
    @Expose
    private int validity;
    @SerializedName("purchaseDate")
    @Expose
    private String purchaseDate;
    @SerializedName("expirationDate")
    @Expose
    private String expirationDate;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("currentDate")
    @Expose
    private String currentDate;

    @SerializedName("TicketPrice")
    @Expose
    private int TicketPrice;

    public int getTicketPrice() {
        return TicketPrice;
    }

    public void setTicketPrice(int ticketPrice) {
        TicketPrice = ticketPrice;
    }

    @SerializedName("user_select")
    @Expose
    private UserSelect userSelect;



    public List<ContestPrice> getContestPriceList() {
        return contestPriceList;
    }

    public void setContestPriceList(List<ContestPrice> contestPriceList) {
        this.contestPriceList = contestPriceList;
    }

    @SerializedName("package")
    @Expose
    private Content packages;

    public Content getPackages() {
        return packages;
    }

    public void setPackages(Content packages) {
        this.packages = packages;
    }

    @SerializedName("contestPrice")
    @Expose
    public List<ContestPrice> contestPriceList = new ArrayList<>();


    private boolean isSelected = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getValidity() {
        return validity;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getLevel() {
        return level;
    }

    public String getCurrentDate() {
        return currentDate;
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

}
