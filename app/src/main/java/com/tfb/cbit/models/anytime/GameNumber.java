package com.tfb.cbit.models.anytime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GameNumber implements Serializable {
    @SerializedName("contestPriceId")
    @Expose
    private String contestPriceId;

    public String getContestPriceId() {
        return contestPriceId;
    }

    public void setContestPriceId(String contestPriceId) {
        this.contestPriceId = contestPriceId;
    }

    public String getGame_no() {
        return game_no;
    }

    public void setGame_no(String game_no) {
        this.game_no = game_no;
    }

    @SerializedName("game_no")
    @Expose
    private String game_no;

}