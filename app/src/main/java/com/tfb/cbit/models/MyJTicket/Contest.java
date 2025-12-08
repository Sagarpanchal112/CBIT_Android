package com.tfb.cbit.models.MyJTicket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Contest {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("ticket_number")
    @Expose
    private String ticket_number;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("j_ticket_id")
    @Expose
    private String j_ticket_id;
    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("ApplyDate")
    @Expose
    private String ApplyDate;
    @SerializedName("WinningAmount")
    @Expose
    private String WinningAmount;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("name")
    @Expose
    private String name;

    public String getHitDate() {
        return HitDate;
    }

    public void setHitDate(String hitDate) {
        HitDate = hitDate;
    }

    @SerializedName("HitDate")
    @Expose
    private String HitDate;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("waiting")
    @Expose
    private String waiting;

    public List<ApproachList> getApproachList() {
        return ApproachList;
    }

    public void setApproachList(List<ApproachList> approachList) {
        ApproachList = approachList;
    }

    @SerializedName("ApproachList")
    @Expose
    public List<ApproachList> ApproachList=new ArrayList<>();


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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getJ_ticket_id() {
        return j_ticket_id;
    }

    public void setJ_ticket_id(String j_ticket_id) {
        this.j_ticket_id = j_ticket_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getWinningAmount() {
        return WinningAmount;
    }

    public void setWinningAmount(String winningAmount) {
        WinningAmount = winningAmount;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWaiting() {
        return waiting;
    }

    public void setWaiting(String waiting) {
        this.waiting = waiting;
    }
}
