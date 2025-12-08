package com.tfb.cbit.models.gamelist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contest {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("rows")
    @Expose
    private String rows;
    @SerializedName("level")
    @Expose
    private int level;
    @SerializedName("closeDate")
    @Expose
    private String closeDate;
    @SerializedName("ansRangeMin")
    @Expose
    private int ansRangeMin;
    @SerializedName("ansRangeMax")
    @Expose
    private int ansRangeMax;
    @SerializedName("game_type")
    @Expose
    private String game_type;

    public String getGame_type() {
        return game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
    }

    public int getAnsRangeMin() {
        return ansRangeMin;
    }

    public void setAnsRangeMin(int ansRangeMin) {
        this.ansRangeMin = ansRangeMin;
    }

    public int getAnsRangeMax() {
        return ansRangeMax;
    }

    public void setAnsRangeMax(int ansRangeMax) {
        this.ansRangeMax = ansRangeMax;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCloseDate() {
        return closeDate;
    }
}
