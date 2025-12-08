package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginMobileModel implements Serializable {
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
        public String getStatuss() {
            return statuss;
        }

        public void setStatuss(String statuss) {
            this.statuss = statuss;
        }

        @SerializedName("statuss")
        @Expose
        public String statuss;

      }


}
