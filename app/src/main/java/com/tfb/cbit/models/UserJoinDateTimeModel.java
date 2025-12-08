package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserJoinDateTimeModel implements Serializable {
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
        public String id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("startDate")
        @Expose
        public String startDate;

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

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getGame_type() {
            return game_type;
        }

        public void setGame_type(String game_type) {
            this.game_type = game_type;
        }

        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("game_type")
        @Expose
        public String game_type;

    }

}
