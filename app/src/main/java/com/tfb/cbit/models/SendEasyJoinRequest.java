package com.tfb.cbit.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SendEasyJoinRequest implements Serializable {
    public String contest_id = "";
    public String contestPriceID ="";

    public String getContest_id() {
        return contest_id;
    }

    public void setContest_id(String contest_id) {
        this.contest_id = contest_id;
    }

    public String getContestPriceID() {
        return contestPriceID;
    }

    public void setContestPriceID(String contestPriceID) {
        this.contestPriceID = contestPriceID;
    }
}
