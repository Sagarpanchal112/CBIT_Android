package com.tfb.cbit.models.contestdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Content {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("currentTime")
    @Expose
    private String currentTime;
    @SerializedName("gameMode")
    @Expose
    private String gameMode;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("rows")
    @Expose
    private int rows;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("ansRangeMin")
    @Expose
    private int ansRangeMin;

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public void setTotalWinAmount(String totalWinAmount) {
        this.totalWinAmount = totalWinAmount;
    }

    @SerializedName("ansRangeMax")
    @Expose
    private int ansRangeMax;



    @SerializedName("boxJson")
    @Expose
    private List<BoxJson> boxJson = null;

    public List<WinningOptions> getWinningOptions() {
        return winningOptions;
    }

    public void setWinningOptions(List<WinningOptions> winningOptions) {
        this.winningOptions = winningOptions;
    }

    @SerializedName("winning_options")
    @Expose
    private List<WinningOptions> winningOptions = null;


    @SerializedName("LockAllData")
    @Expose
    private List<LockAllData> LockAllData = null;

    public List<com.tfb.cbit.models.contestdetails.LockAllData> getLockAllData() {
        return LockAllData;
    }

    public void setLockAllData(List<com.tfb.cbit.models.contestdetails.LockAllData> lockAllData) {
        LockAllData = lockAllData;
    }

    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("blue")
    @Expose
    private String blue;
    @SerializedName("red")
    @Expose
    private String red;
    @SerializedName("level")
    @Expose
    private int level;
    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("gameStatus")
    @Expose
    private String gameStatus;

    @SerializedName("tickets")
    @Expose
    private List<Ticket> tickets = new ArrayList<>();
    @SerializedName("totalWinAmount")
    @Expose
    private String totalWinAmount;
    @SerializedName("totalCCWinAmount")
    @Expose
    private String totalCCWinAmount;
    @SerializedName("scrollerContent")
    @Expose
    private String scrollerContent;

    @SerializedName("game_type")
    @Expose
    private String game_type;

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("nowin")
    @Expose
    private String nowin;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIs_anytimegame() {
        return is_anytimegame;
    }

    public void setIs_anytimegame(int is_anytimegame) {
        this.is_anytimegame = is_anytimegame;
    }

    @SerializedName("is_anytimegame")
    @Expose
    public int is_anytimegame;

    public String getNowin() {
        return nowin;
    }

    public void setNowin(String nowin) {
        this.nowin = nowin;
    }

    public String getGame_type() {
            return game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
    }

    public String getBlue() {
        return blue;
    }

    public void setBlue(String blue) {
        this.blue = blue;
    }

    public String getRed() {
        return red;
    }

    public void setRed(String red) {
        this.red = red;
    }

    public String getTotalCCWinAmount() {
        return totalCCWinAmount;
    }

    public void setTotalCCWinAmount(String totalCCWinAmount) {
        this.totalCCWinAmount = totalCCWinAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public List<BoxJson> getBoxJson() {
        return boxJson;
    }

    public void setBoxJson(List<BoxJson> boxJson) {
        this.boxJson = boxJson;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public String getTotalWinAmount() {
        return totalWinAmount;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getScrollerContent() {
        return scrollerContent;
    }

    public void setScrollerContent(String scrollerContent) {
        this.scrollerContent = scrollerContent;
    }
}
