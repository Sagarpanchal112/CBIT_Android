package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UpdateVersionModel implements Serializable {
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
        @SerializedName("contest")
        @Expose
        public List<Contest> contest=null;

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
        @SerializedName("update_type")
        @Expose
        public String update_type;
        @SerializedName("platform")
        @Expose
        public String platform;
        @SerializedName("link")
        @Expose
        public String link;
        @SerializedName("version")
        @Expose
        public String version;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUpdate_type() {
            return update_type;
        }

        public void setUpdate_type(String update_type) {
            this.update_type = update_type;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
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

        @SerializedName("status")
        @Expose
        public int status;
        @SerializedName("created_at")
        @Expose
        public String created_at;
        @SerializedName("updated_at")
        @Expose
        public String updated_at;

        }

}
