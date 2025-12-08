package com.tfb.cbit.models.passbook;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PassBookModel {

    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("content")
    @Expose
    private List<Content> content = null;
    @SerializedName("message")
    @Expose
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDropdownArray() {
        return dropdownArray;
    }

    public void setDropdownArray(List<String> dropdownArray) {
        this.dropdownArray = dropdownArray;
    }

    @SerializedName("DropDown")
    @Expose
    private List<String> dropdownArray = null;

    public List<DisplayValuess> getDisplayValuessList() {
        return displayValuessList;
    }

    public void setDisplayValuessList(List<DisplayValuess> displayValuessList) {
        this.displayValuessList = displayValuessList;
    }

    @SerializedName("DisplayValuess")
    @Expose
    private List<DisplayValuess> displayValuessList = null;

    public List<String> getDisplayValueArray() {
        return displayValueArray;
    }

    public void setDisplayValueArray(List<String> displayValueArray) {
        this.displayValueArray = displayValueArray;
    }

    @SerializedName("DisplayValue")
    @Expose
    private List<String> displayValueArray = null;

    public class DisplayValuess {
        @SerializedName("value")
        @Expose
        public String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        @SerializedName("display")
        @Expose
        public String display;

    }


}
