package com.tfb.cbit.models.add_contest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {
    @SerializedName("contestId")
    @Expose
    private String contestId;

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }
}
