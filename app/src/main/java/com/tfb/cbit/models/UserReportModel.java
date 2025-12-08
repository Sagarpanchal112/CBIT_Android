package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserReportModel implements Serializable {
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
    public ArrayList<Content> content = new ArrayList<>();

    public class Content {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("senderID")
        @Expose
        public int senderID;
        @SerializedName("RecipientID")
        @Expose
        public int RecipientID;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @SerializedName("imageUrl")
        @Expose
        public String imageUrl;

        public String getVoiceUrl() {
            return voiceUrl;
        }

        public void setVoiceUrl(String voiceUrl) {
            this.voiceUrl = voiceUrl;
        }

        @SerializedName("voiceUrl")
        @Expose
        public String voiceUrl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSenderID() {
            return senderID;
        }

        public void setSenderID(int senderID) {
            this.senderID = senderID;
        }

        public int getRecipientID() {
            return RecipientID;
        }

        public void setRecipientID(int recipientID) {
            RecipientID = recipientID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getIs_read() {
            return is_read;
        }

        public void setIs_read(int is_read) {
            this.is_read = is_read;
        }

        public String getRead_at() {
            return read_at;
        }

        public void setRead_at(String read_at) {
            this.read_at = read_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getReadTime() {
            return readTime;
        }

        public void setReadTime(String readTime) {
            this.readTime = readTime;
        }

        public String getDeliveredTime() {
            return deliveredTime;
        }

        public void setDeliveredTime(String deliveredTime) {
            this.deliveredTime = deliveredTime;
        }

        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("is_read")
        @Expose
        public int is_read;
        @SerializedName("read_at")
        @Expose
        public String read_at;
        @SerializedName("created_at")
        @Expose
        public String created_at;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("readTime")
        @Expose
        public String readTime;
        @SerializedName("deliveredTime")
        @Expose
        public String deliveredTime;

    }

}
