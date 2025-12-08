package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DefaultJoinTicket implements Serializable {
    @SerializedName("statusCode")
    @Expose
    public int statusCode;

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

    public Content getContents() {
        return content;
    }

    public void setContents(Content contents) {
        this.content = contents;
    }

    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("content")
    @Expose
    public Content content = null;

    public class Content {
        @SerializedName("contest")
        @Expose
        public List<Contest> contest = null;

        public List<Contest> getContest() {
            return contest;
        }

        public void setContest(List<Contest> contest) {
            this.contest = contest;
        }
    }
    public class Contest {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("price")
        @Expose
        public double price;
        @SerializedName("JExcess")
        @Expose
        public String JExcess;
        @SerializedName("price_range_from")
        @Expose
        public String price_range_from;
        @SerializedName("price_range_to")
        @Expose
        public String price_range_to;
        @SerializedName("redenption_from")
        @Expose
        public String redenption_from;
        @SerializedName("redenption_to")
        @Expose
        public String redenption_to;
        @SerializedName("max_apply_per_day")
        @Expose
        public String max_apply_per_day;

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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getJExcess() {
            return JExcess;
        }

        public void setJExcess(String JExcess) {
            this.JExcess = JExcess;
        }

        public String getAlpha_series() {
            return alpha_series;
        }

        public void setAlpha_series(String alpha_series) {
            this.alpha_series = alpha_series;
        }

        public String getNumber_series() {
            return number_series;
        }

        public void setNumber_series(String number_series) {
            this.number_series = number_series;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        @SerializedName("alpha_series")
        @Expose
        public String alpha_series;
        @SerializedName("number_series")
        @Expose
        public String number_series;
        @SerializedName("created_at")
        @Expose
        public String created_at;
        @SerializedName("isSelected")
        @Expose
        public boolean isSelected;



    }

}
