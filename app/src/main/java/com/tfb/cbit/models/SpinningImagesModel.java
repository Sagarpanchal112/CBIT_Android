package com.tfb.cbit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SpinningImagesModel implements Serializable {
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

    public List<Content> getContents() {
        return content;
    }

    public void setContents(List<Content> contents) {
        this.content = contents;
    }

    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("content")
    @Expose
    public List<Content> content = null;

    public class Content {
        @SerializedName("id")
        @Expose
        public int id=0;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        @SerializedName("status")
        @Expose
        public int status=0;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getCategoryID() {
            return categoryID;
        }

        public void setCategoryID(int categoryID) {
            this.categoryID = categoryID;
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

        @SerializedName("categoryID")
        @Expose
        public int categoryID=0;
        @SerializedName("name")
        @Expose
        public String name="";
        @SerializedName("image")
        @Expose
        public String image="";
          @SerializedName("created_at")
        @Expose
        public String created_at="";
    }

}
