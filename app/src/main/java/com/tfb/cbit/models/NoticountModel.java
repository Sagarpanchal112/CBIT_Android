package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NoticountModel implements Serializable {
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
        @SerializedName("count")
        @Expose
        public int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }


}
