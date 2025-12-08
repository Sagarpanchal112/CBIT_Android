package com.tfb.cbit.models;

import java.io.Serializable;

public class SendAutoRenewRequest implements Serializable {
    public String contest_time = "";
    public String price = "";
    public int status = 0;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContest_time() {
        return contest_time;
    }

    public void setContest_time(String contest_time) {
        this.contest_time = contest_time;
    }

   }
