package com.tfb.cbit.models.JWaitingRoom;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Contest {


    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("image")
    @Expose
    private String image;

    public String getIsApproach() {
        return isApproach;
    }

    public void setIsApproach(String isApproach) {
        this.isApproach = isApproach;
    }

    @SerializedName("isApproach")
    @Expose
    private String isApproach;


    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("id")
    @Expose
    private String id;
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

    public String getRedenption_from() {
        return redenption_from;
    }

    public void setRedenption_from(String redenption_from) {
        this.redenption_from = redenption_from;
    }

    public String getRedenption_to() {
        return redenption_to;
    }

    public void setRedenption_to(String redenption_to) {
        this.redenption_to = redenption_to;
    }

    @SerializedName("redenption_from")
    @Expose
    private String redenption_from;
    @SerializedName("Down")
    @Expose
    private String Down;
    @SerializedName("Up")
    @Expose
    private String Up;

    public String getDown() {
        return Down;
    }

    public void setDown(String down) {
        Down = down;
    }

    public String getUp() {
        return Up;
    }

    public void setUp(String up) {
        Up = up;
    }

    public String getApproachCashbackPer() {
        return ApproachCashbackPer;
    }

    public void setApproachCashbackPer(String approachCashbackPer) {
        ApproachCashbackPer = approachCashbackPer;
    }

    public String getApproachCashback() {
        return ApproachCashback;
    }

    public void setApproachCashback(String approachCashback) {
        ApproachCashback = approachCashback;
    }

    public String getCashbackUpto() {
        return CashbackUpto;
    }

    public void setCashbackUpto(String cashbackUpto) {
        CashbackUpto = cashbackUpto;
    }

    @SerializedName("ApproachCashbackPer")
    @Expose
    private String ApproachCashbackPer;
    @SerializedName("ApproachCashback")
    @Expose
    private String ApproachCashback;
    @SerializedName("CashbackUpto")
    @Expose
    private String CashbackUpto;
    @SerializedName("redenption_to")
    @Expose
    private String redenption_to;
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
