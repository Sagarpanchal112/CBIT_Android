package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PopupDataModel implements Serializable {
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

        public List<PopUpData> getPopUpData() {
            return popUpData;
        }

        public void setPopUpData(List<PopUpData> popUpData) {
            this.popUpData = popUpData;
        }

        @SerializedName("popUpData")
        @Expose
        public List<PopUpData> popUpData = null;

        public int getUserData() {
            return userData;
        }

        public void setUserData(int userData) {
            this.userData = userData;
        }

        @SerializedName("userData")
        @Expose
        public int userData;

    }

    public class PopUpData {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("content")
        @Expose
        public String content;
        @SerializedName("link")
        @Expose
        public String link;
        @SerializedName("placement")
        @Expose
        public String placement;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }



        @SerializedName("status")
        @Expose
        public int status;
        @SerializedName("created_at")
        @Expose
        public String created_at;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getPlacement() {
            return placement;
        }

        public void setPlacement(String placement) {
            this.placement = placement;
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

        public int getIs_cancel() {
            return is_cancel;
        }

        public void setIs_cancel(int is_cancel) {
            this.is_cancel = is_cancel;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        @SerializedName("updated_at")
        @Expose
        public String updated_at;
        @SerializedName("is_cancel")
        @Expose
        public int is_cancel;
        @SerializedName("device")
        @Expose
        public String device;

    }
}
