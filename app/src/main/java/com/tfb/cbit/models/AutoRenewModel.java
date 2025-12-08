package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AutoRenewModel implements Serializable {
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


    public List<Content> getContent() {
        return content;
    }

    public List<AutorenewTable> getAutorenewTable() {
        return autorenewTable;
    }

    public void setAutorenewTable(List<AutorenewTable> autorenewTable) {
        this.autorenewTable = autorenewTable;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    @SerializedName("content")
    @Expose
    public List<Content> content;

    public List<Price> getPrice() {
        return price;
    }

    public void setPrice(List<Price> price) {
        this.price = price;
    }

    @SerializedName("price")
    @Expose
    public List<Price> price;
    @SerializedName("autorenewTable")
    @Expose
    public List<AutorenewTable> autorenewTable;

    public class Content {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("contestTime")
        @Expose
        public String contestTime;

        public ArrayList<Integer> getContest_price() {
            return contest_price;
        }

        public void setContest_price(ArrayList<Integer> contest_price) {
            this.contest_price = contest_price;
        }

        @SerializedName("contest_price")
        @Expose
        public ArrayList<Integer> contest_price;

        public String getContestTime() {
            return contestTime;
        }

        public void setContestTime(String contestTime) {
            this.contestTime = contestTime;
        }

        public String getContest() {
            return contest;
        }

        public void setContest(String contest) {
            this.contest = contest;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @SerializedName("contest")
        @Expose
        public String contest;
        @SerializedName("status")
        @Expose
        public int status;

    }

    public class Price {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("autorenew_time")
        @Expose
        public String autorenew_time;

        public String getAutorenew_time() {
            return autorenew_time;
        }

        public void setAutorenew_time(String autorenew_time) {
            this.autorenew_time = autorenew_time;
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

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        @SerializedName("price")
        @Expose
        public int price;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public boolean isSelected = false;

    }

    public class AutorenewTable {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("autorenew_time")
        @Expose
        public String autorenew_time;
        @SerializedName("special_contest")
        @Expose
        public String special_contest;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("CreatedBy")
        @Expose
        public String CreatedBy;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAutorenew_time() {
            return autorenew_time;
        }

        public void setAutorenew_time(String autorenew_time) {
            this.autorenew_time = autorenew_time;
        }

        public String getSpecial_contest() {
            return special_contest;
        }

        public void setSpecial_contest(String special_contest) {
            this.special_contest = special_contest;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreatedBy() {
            return CreatedBy;
        }

        public void setCreatedBy(String createdBy) {
            CreatedBy = createdBy;
        }

        public String getCreatedOn() {
            return CreatedOn;
        }

        public void setCreatedOn(String createdOn) {
            CreatedOn = createdOn;
        }

        public String getTwentyfourhourformat() {
            return twentyfourhourformat;
        }

        public void setTwentyfourhourformat(String twentyfourhourformat) {
            this.twentyfourhourformat = twentyfourhourformat;
        }

        @SerializedName("CreatedOn")
        @Expose
        public String CreatedOn;
        @SerializedName("contest_Id")
        @Expose
        public int contest_Id;
        @SerializedName("contest")
        @Expose
        public String contest;
        @SerializedName("contest_status")
        @Expose
        public int contest_status;

        public int getContest_Id() {
            return contest_Id;
        }

        public void setContest_Id(int contest_Id) {
            this.contest_Id = contest_Id;
        }

        public String getContest() {
            return contest;
        }

        public void setContest(String contest) {
            this.contest = contest;
        }

        public int getContest_status() {
            return contest_status;
        }

        public void setContest_status(int contest_status) {
            this.contest_status = contest_status;
        }

        public ArrayList<Integer> getContest_price() {
            return contest_price;
        }

        public void setContest_price(ArrayList<Integer> contest_price) {
            this.contest_price = contest_price;
        }

        @SerializedName("twentyfourhourformat")
        @Expose
        public String twentyfourhourformat;
        @SerializedName("contest_price")
        @Expose
        public ArrayList<Integer> contest_price;

    }


}
