package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EasyJoinModel implements Serializable {
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
        public List<Contest> getContest() {
            return contest;
        }

        public void setContest(List<Contest> contest) {
            this.contest = contest;
        }

        @SerializedName("contest")
        @Expose
        public List<Contest> contest;

    }

    public class Contest {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("startDate")
        @Expose
        public String startDate;
        @SerializedName("type")
        @Expose
        public int type;
        @SerializedName("rows")
        @Expose
        public int rows;
        @SerializedName("game_type")
        @Expose
        public String game_type;
        public boolean isSelected;
        public boolean isSwitchSelected;

        public boolean isSwitchSelected() {
            return isSwitchSelected;
        }

        public void setSwitchSelected(boolean switchSelected) {
            isSwitchSelected = switchSelected;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
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

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getCloseDate() {
            return closeDate;
        }

        public void setCloseDate(String closeDate) {
            this.closeDate = closeDate;
        }

        public List<PrinceData> getPrinceData() {
            return princeData;
        }

        public void setPrinceData(List<PrinceData> princeData) {
            this.princeData = princeData;
        }

        @SerializedName("ansRangeMin")
        @Expose
        public int ansRangeMin;
        @SerializedName("ansRangeMax")
        @Expose
        public int ansRangeMax;
        @SerializedName("level")
        @Expose
        public int level;
        @SerializedName("closeDate")
        @Expose
        public String closeDate;
        @SerializedName("princeData")
        @Expose
        public List<PrinceData> princeData;

    }

    public class PrinceData {
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("contestId")
        @Expose
        public String contestId;
        @SerializedName("closeDate")
        @Expose
        public String closeDate;
        @SerializedName("amount")
        @Expose
        public String amount;
        @SerializedName("maxWinner")
        @Expose
        public String maxWinner;
        @SerializedName("minJoin")
        @Expose
        public String minJoin;
        @SerializedName("maxJoin")
        @Expose
        public String maxJoin;
        @SerializedName("bracketSize")
        @Expose
        public String bracketSize;
        @SerializedName("commision")
        @Expose
        public String commision;
        @SerializedName("MaxWinJTicket")
        @Expose
        public String MaxWinJTicket;
        @SerializedName("JTicketCommision")
        @Expose
        public String JTicketCommision;
        public boolean isSelected;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContestId() {
            return contestId;
        }

        public void setContestId(String contestId) {
            this.contestId = contestId;
        }

        public String getCloseDate() {
            return closeDate;
        }

        public void setCloseDate(String closeDate) {
            this.closeDate = closeDate;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getMaxWinner() {
            return maxWinner;
        }

        public void setMaxWinner(String maxWinner) {
            this.maxWinner = maxWinner;
        }

        public String getMinJoin() {
            return minJoin;
        }

        public void setMinJoin(String minJoin) {
            this.minJoin = minJoin;
        }

        public String getMaxJoin() {
            return maxJoin;
        }

        public void setMaxJoin(String maxJoin) {
            this.maxJoin = maxJoin;
        }

        public String getBracketSize() {
            return bracketSize;
        }

        public void setBracketSize(String bracketSize) {
            this.bracketSize = bracketSize;
        }

        public String getCommision() {
            return commision;
        }

        public void setCommision(String commision) {
            this.commision = commision;
        }

        public String getMaxWinJTicket() {
            return MaxWinJTicket;
        }

        public void setMaxWinJTicket(String maxWinJTicket) {
            MaxWinJTicket = maxWinJTicket;
        }

        public String getJTicketCommision() {
            return JTicketCommision;
        }

        public void setJTicketCommision(String JTicketCommision) {
            this.JTicketCommision = JTicketCommision;
        }

        public String getjTicketID() {
            return jTicketID;
        }

        public void setjTicketID(String jTicketID) {
            this.jTicketID = jTicketID;
        }

        public String getNo_of_players() {
            return no_of_players;
        }

        public void setNo_of_players(String no_of_players) {
            this.no_of_players = no_of_players;
        }

        public String getGame_played() {
            return game_played;
        }

        public void setGame_played(String game_played) {
            this.game_played = game_played;
        }

        public String getPlayers_played() {
            return players_played;
        }

        public void setPlayers_played(String players_played) {
            this.players_played = players_played;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        @SerializedName("jTicketID")
        @Expose
        public String jTicketID;
        @SerializedName("no_of_players")
        @Expose
        public String no_of_players;
        @SerializedName("game_played")
        @Expose
        public String game_played;
        @SerializedName("players_played")
        @Expose
        public String players_played;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("created_at")
        @Expose
        public String created_at;
    }

}
