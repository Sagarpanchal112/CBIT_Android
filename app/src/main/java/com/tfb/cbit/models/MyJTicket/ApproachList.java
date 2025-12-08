package com.tfb.cbit.models.MyJTicket;

import java.io.Serializable;

public class ApproachList implements Serializable {
    public int j_ticket_user_approach_id;
    public int j_ticket_user_id_from;
    public int j_ticket_user_id_to;
    public int offer;
    public int negotiate;

    public double getCashback() {
        return cashback;
    }

    public void setCashback(double cashback) {
        this.cashback = cashback;
    }

    public double cashback;

    public int getJ_ticket_user_approach_id() {
        return j_ticket_user_approach_id;
    }

    public void setJ_ticket_user_approach_id(int j_ticket_user_approach_id) {
        this.j_ticket_user_approach_id = j_ticket_user_approach_id;
    }

    public int getJ_ticket_user_id_from() {
        return j_ticket_user_id_from;
    }

    public void setJ_ticket_user_id_from(int j_ticket_user_id_from) {
        this.j_ticket_user_id_from = j_ticket_user_id_from;
    }

    public int getJ_ticket_user_id_to() {
        return j_ticket_user_id_to;
    }

    public void setJ_ticket_user_id_to(int j_ticket_user_id_to) {
        this.j_ticket_user_id_to = j_ticket_user_id_to;
    }

    public int getUser_from() {
        return user_from;
    }

    public void setUser_from(int user_from) {
        this.user_from = user_from;
    }

    public int getUser_to() {
        return user_to;
    }

    public void setUser_to(int user_to) {
        this.user_to = user_to;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int user_from;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getJ_ticket_id() {
        return j_ticket_id;
    }

    public void setJ_ticket_id(int j_ticket_id) {
        this.j_ticket_id = j_ticket_id;
    }

    public int getWaitingNumber() {
        return WaitingNumber;
    }

    public void setWaitingNumber(int waitingNumber) {
        WaitingNumber = waitingNumber;
    }

    public void setOffer(int offer) {
        this.offer = offer;
    }

    public void setNegotiate(int negotiate) {
        this.negotiate = negotiate;
    }

    public int getOffer() {
        return offer;
    }

    public int getNegotiate() {
        return negotiate;
    }

    public int getAccept() {
        return accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }

    public int user_to;
    public int accept;
    public String created_at;
    public String userName;
    public int j_ticket_id;
    public int WaitingNumber;

}
