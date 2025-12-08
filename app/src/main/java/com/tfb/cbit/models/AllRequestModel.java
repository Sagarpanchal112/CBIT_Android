package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllRequestModel implements Serializable {
    @SerializedName("statusCode")
    @Expose
    public int statusCode;
    @SerializedName("message")
    @Expose
    public String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    @SerializedName("content")
    @Expose
    public Content content;

    public class Content {
        public List<AllRequest> getAllRequestArrayList() {
            return allRequestArrayList;
        }

        public void setAllRequestArrayList(List<AllRequest> allRequestArrayList) {
            this.allRequestArrayList = allRequestArrayList;
        }

        @SerializedName("allRequest")
        @Expose
        public List<AllRequest> allRequestArrayList = null;

    }

    public class AllRequest {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("StartTime")
        @Expose
        public String StartTime;
        @SerializedName("group_id")
        @Expose
        public int group_id;
        @SerializedName("contestId")
        @Expose
        public int contestId;
        @SerializedName("user_id")
        @Expose
        public int user_id;
        @SerializedName("request")
        @Expose
        public int request;
        @SerializedName("created_at")
        @Expose
        public String created_at;
        @SerializedName("updated_at")
        @Expose
        public String updated_at;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("startDate")
        @Expose
        public String startDate;
        @SerializedName("validityDate")
        @Expose
        public String validityDate;
        @SerializedName("lock_style")
        @Expose
        public String lock_style;
        @SerializedName("type")
        @Expose
        public int type;
        @SerializedName("cols")
        @Expose
        public int cols;
        @SerializedName("rows")
        @Expose
        public int rows;
        @SerializedName("game_type")
        @Expose
        public String game_type;

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        @SerializedName("group_name")
        @Expose
        public String group_name;
        @SerializedName("no_of_items")
        @Expose
        public int no_of_items;
        @SerializedName("ansRangeMin")
        @Expose
        public int ansRangeMin;
        @SerializedName("ansRangeMax")
        @Expose
        public int ansRangeMax;
        @SerializedName("winning_options")
        @Expose
        public String winning_options;
        @SerializedName("box_json")
        @Expose
        private List<BoxJson> boxJson = null;
        @SerializedName("answer")
        @Expose
        public int answer;
        @SerializedName("status")
        @Expose
        public int status;
        @SerializedName("no_of_players")
        @Expose
        public int no_of_players;
        @SerializedName("is_anytimegame")
        @Expose
        public int is_anytimegame;
        @SerializedName("auto_renew")
        @Expose
        public int auto_renew;
        @SerializedName("gameMode")
        @Expose
        public String gameMode;
        @SerializedName("hostPackgeId")
        @Expose
        public String hostPackgeId;
        @SerializedName("userId")
        @Expose
        public String userId;

        public int getTicketSold() {
            return ticketSold;
        }

        public void setTicketSold(int ticketSold) {
            this.ticketSold = ticketSold;
        }

        @SerializedName("ticketSold")
        @Expose
        public int ticketSold;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStartTime() {
            return StartTime;
        }

        public void setStartTime(String startTime) {
            StartTime = startTime;
        }

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public int getContestId() {
            return contestId;
        }

        public void setContestId(int contestId) {
            this.contestId = contestId;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getRequest() {
            return request;
        }

        public void setRequest(int request) {
            this.request = request;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
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

        public String getValidityDate() {
            return validityDate;
        }

        public void setValidityDate(String validityDate) {
            this.validityDate = validityDate;
        }

        public String getLock_style() {
            return lock_style;
        }

        public void setLock_style(String lock_style) {
            this.lock_style = lock_style;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getCols() {
            return cols;
        }

        public void setCols(int cols) {
            this.cols = cols;
        }

        public int getRows() {
            return rows;
        }

        public void setRows(int rows) {
            this.rows = rows;
        }

        public String getGame_type() {
            return game_type;
        }

        public void setGame_type(String game_type) {
            this.game_type = game_type;
        }

        public int getNo_of_items() {
            return no_of_items;
        }

        public void setNo_of_items(int no_of_items) {
            this.no_of_items = no_of_items;
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

        public String getWinning_options() {
            return winning_options;
        }

        public void setWinning_options(String winning_options) {
            this.winning_options = winning_options;
        }

        public List<BoxJson> getBoxJson() {
            return boxJson;
        }

        public void setBoxJson(List<BoxJson> boxJson) {
            this.boxJson = boxJson;
        }

        public int getAnswer() {
            return answer;
        }

        public void setAnswer(int answer) {
            this.answer = answer;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getNo_of_players() {
            return no_of_players;
        }

        public void setNo_of_players(int no_of_players) {
            this.no_of_players = no_of_players;
        }

        public int getIs_anytimegame() {
            return is_anytimegame;
        }

        public void setIs_anytimegame(int is_anytimegame) {
            this.is_anytimegame = is_anytimegame;
        }

        public int getAuto_renew() {
            return auto_renew;
        }

        public void setAuto_renew(int auto_renew) {
            this.auto_renew = auto_renew;
        }

        public String getGameMode() {
            return gameMode;
        }

        public void setGameMode(String gameMode) {
            this.gameMode = gameMode;
        }

        public String getHostPackgeId() {
            return hostPackgeId;
        }

        public void setHostPackgeId(String hostPackgeId) {
            this.hostPackgeId = hostPackgeId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public String getGameType() {
            return GameType;
        }

        public void setGameType(String gameType) {
            GameType = gameType;
        }

        @SerializedName("priority")
        @Expose
        public int priority;
        @SerializedName("GameType")
        @Expose
        public String GameType;
    }

    public class BoxJson {
        @SerializedName("color")
        @Expose
        private String color;
        @SerializedName("sign")
        @Expose
        private String sign;
        @SerializedName("value")
        @Expose
        private int value;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

}
