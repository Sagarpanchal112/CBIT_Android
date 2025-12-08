package com.tfb.cbit.models.approch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserContest {
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("image")
    @Expose
    private String image;


    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("id")
    @Expose
    private String id;

    public String getRedenption_to() {
        return redenption_to;
    }

    public void setRedenption_to(String redenption_to) {
        this.redenption_to = redenption_to;
    }

    public String getRedenption_from() {
        return redenption_from;
    }

    public void setRedenption_from(String redenption_from) {
        this.redenption_from = redenption_from;
    }

    @SerializedName("redenption_to")
    @Expose
    private String redenption_to;
    @SerializedName("redenption_from")
    @Expose
    private String redenption_from;
    @SerializedName("ticket_number")
    @Expose
    private String ticket_number;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("waiting")
    @Expose
    private String waiting;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("j_ticket_id")
    @Expose
    private String j_ticket_id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("ApplyDate")
    @Expose
    private String ApplyDate;
    @SerializedName("HitDate")
    @Expose
    private String HitDate;
    @SerializedName("WinningAmount")
    @Expose
    private String WinningAmount;
    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("contestPriceId")
    @Expose
    private String contestPriceId;

    public String getHitDate() {
        return HitDate;
    }

    public void setHitDate(String hitDate) {
        HitDate = hitDate;
    }

    public String getWinningAmount() {
        return WinningAmount;
    }

    public void setWinningAmount(String winningAmount) {
        WinningAmount = winningAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getContestPriceId() {
        return contestPriceId;
    }

    public void setContestPriceId(String contestPriceId) {
        this.contestPriceId = contestPriceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @SerializedName("type")
    @Expose
    private String type;

    public ArrayList<com.tfb.cbit.models.MyJTicket.ApproachList> getApproachList() {
        return ApproachList;
    }

    public void setApproachList(ArrayList<com.tfb.cbit.models.MyJTicket.ApproachList> approachList) {
        ApproachList = approachList;
    }

    @SerializedName("ApproachList")
    @Expose
    private ArrayList<com.tfb.cbit.models.MyJTicket.ApproachList> ApproachList;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicket_number() {
        return ticket_number;
    }

    public void setTicket_number(String ticket_number) {
        this.ticket_number = ticket_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWaiting() {
        return waiting;
    }

    public void setWaiting(String waiting) {
        this.waiting = waiting;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getJ_ticket_id() {
        return j_ticket_id;
    }

    public void setJ_ticket_id(String j_ticket_id) {
        this.j_ticket_id = j_ticket_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplyDate() {
        return ApplyDate;
    }

    public void setApplyDate(String applyDate) {
        ApplyDate = applyDate;
    }
}
