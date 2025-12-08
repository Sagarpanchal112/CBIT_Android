package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportIssueModel implements Serializable {
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

    public ArrayList<Content> getContent() {
        return content;
    }

    public void setContent(ArrayList<Content> content) {
        this.content = content;
    }

    @SerializedName("content")
    @Expose
    public ArrayList<Content> content=new ArrayList<>();

    public class Content {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("is_imp")
        @Expose
        public int is_imp;
        @SerializedName("CreatedBy")
        @Expose
        public int CreatedBy;
        @SerializedName("report_title")
        @Expose
        public String report_title;
        @SerializedName("CreatedOn")
        @Expose
        public String CreatedOn;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIs_imp() {
            return is_imp;
        }

        public void setIs_imp(int is_imp) {
            this.is_imp = is_imp;
        }

        public int getCreatedBy() {
            return CreatedBy;
        }

        public void setCreatedBy(int createdBy) {
            CreatedBy = createdBy;
        }

        public String getReport_title() {
            return report_title;
        }

        public void setReport_title(String report_title) {
            this.report_title = report_title;
        }

        public String getCreatedOn() {
            return CreatedOn;
        }

        public void setCreatedOn(String createdOn) {
            CreatedOn = createdOn;
        }
    }

}
