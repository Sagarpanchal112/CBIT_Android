package com.tfb.cbit.models.private_group;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PrivateGroupResponse implements Serializable {
    @Expose
    @SerializedName("statusCode")
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


    public List<Content> getContent() {
        return content;
    }

    @Expose
    @SerializedName("message")
    public String message;

    public void setContent(List<Content> content) {
        this.content = content;
    }

    @Expose
    @SerializedName("content")
    public List<Content> content=null;

    public static class Content {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("private_group_name")
        public String private_group_name;
        @Expose
        @SerializedName("user_id")
        public int user_id;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Expose
        @SerializedName("count")
        public int count;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPrivate_group_name() {
            return private_group_name;
        }

        public void setPrivate_group_name(String private_group_name) {
            this.private_group_name = private_group_name;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
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

        @Expose
        @SerializedName("created_at")
        public String created_at;
        @Expose
        @SerializedName("updated_at")
        public String updated_at;

    }
}
