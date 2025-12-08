package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AllUserRequestModel implements Serializable {
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
        public List<AllRequest> getAllRequestArrayList() {
            return allRequestArrayList;
        }

        public void setAllRequestArrayList(List<AllRequest> allRequestArrayList) {
            this.allRequestArrayList = allRequestArrayList;
        }

        @SerializedName("allRequest")
        @Expose
        public List<AllRequest> allRequestArrayList = null;

    }

    public class AllRequest {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("group_id")
        @Expose
        public int group_id;
        @SerializedName("user_id")
        @Expose
        public int user_id;
        @SerializedName("request")
        @Expose
        public int request;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getRequest() {
            return request;
        }

        public void setRequest(int request) {
            this.request = request;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMiddelName() {
            return middelName;
        }

        public void setMiddelName(String middelName) {
            this.middelName = middelName;
        }

        @SerializedName("status")
        @Expose
        public int status;
        @SerializedName("created_at")
        @Expose
        public String created_at;
        @SerializedName("updated_at")
        @Expose
        public String updated_at;
        @SerializedName("firstName")
        @Expose
        public String firstName;
        @SerializedName("lastName")
        @Expose
        public String lastName;
        @SerializedName("middelName")
        @Expose
        public String middelName;
    }
}
