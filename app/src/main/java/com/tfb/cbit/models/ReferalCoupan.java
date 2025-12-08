package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReferalCoupan implements Serializable {
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


    @SerializedName("message")
    @Expose
    public String message;

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    @SerializedName("content")
    @Expose
    public List<Content> content = null;

    public class Content {
        @SerializedName("User")
        @Expose
        public String User;

        public String getUser() {
            return User;
        }

        public void setUser(String user) {
            User = user;
        }

        public String getReffralList() {
            return ReffralList;
        }

        public void setReffralList(String reffralList) {
            ReffralList = reffralList;
        }

        @SerializedName("ReffralList")
        @Expose
        public String ReffralList;

    }

}
