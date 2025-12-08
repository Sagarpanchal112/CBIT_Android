package com.tfb.cbit.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnyTimeSpinningCatList implements Serializable {
    public  int statusCode;
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

    public Content content;
    public class Content implements Serializable{

        public List<Lst> getLst() {
            return lst;
        }

        public void setLst(List<Lst> lst) {
            this.lst = lst;
        }

        public List<Lst> lst=new ArrayList<>();


    }

    public class Lst implements Serializable{
        public int id;
        public String name;
        public String status;
        public String created_at;

        public List<Items> getItems() {
            return items;
        }

        public void setItems(List<Items> items) {
            this.items = items;
        }

        public List<Items> items=new ArrayList<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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



    }
    public static class Items implements Serializable {
        public int id;
        public String name;
        public String image;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getCategoryID() {
            return categoryID;
        }

        public void setCategoryID(String categoryID) {
            this.categoryID = categoryID;
        }

        public String created_at;
        public String categoryID;
    }
    }
