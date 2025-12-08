package com.tfb.cbit.models.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("id")
    @Expose
    private String id;

    public int getContestPriceID() {
        return contestPriceID;
    }

    public void setContestPriceID(int contestPriceID) {
        this.contestPriceID = contestPriceID;
    }

    @SerializedName("contestPriceID")
    @Expose
    private int contestPriceID;
    @SerializedName("name")
    @Expose
    private String name;

    public String getIs_watch() {
        return is_watch;
    }

    public void setIs_watch(String is_watch) {
        this.is_watch = is_watch;
    }

    @SerializedName("contest_date")
    @Expose
    private String contestDate;
    @SerializedName("contest_time")
    @Expose
    private String contestTime;
    @SerializedName("is_watch")
    @Expose
    private String is_watch;

    public String getGame_date() {
        return game_date;
    }

    public void setGame_date(String game_date) {
        this.game_date = game_date;
    }

    public String getGame_time() {
        return game_time;
    }

    public void setGame_time(String game_time) {
        this.game_time = game_time;
    }

    @SerializedName("game_date")
    @Expose
    private String game_date="";
    @SerializedName("game_time")
    @Expose
    private String game_time;
    @SerializedName("game_type")
    @Expose
    private String game_type;

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    @SerializedName("game")
    @Expose
    private String game;

    public String getGame_type() {
        return game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
    }

    @SerializedName("type")
    @Expose
    private int type;

    public int getGame_no() {
        return game_no;
    }

    public void setGame_no(int game_no) {
        this.game_no = game_no;
    }

    @SerializedName("game_no")
    @Expose
    private int game_no;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContestDate() {
        return contestDate;
    }

    public void setContestDate(String contestDate) {
        this.contestDate = contestDate;
    }

    public String getContestTime() {
        return contestTime;
    }

    public void setContestTime(String contestTime) {
        this.contestTime = contestTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
