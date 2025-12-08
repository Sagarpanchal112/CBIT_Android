package com.tfb.cbit.models.statecity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Content {
    @SerializedName("State")
    @Expose
    private List<StateCityDetailsModel> StateList = null;
 @SerializedName("City")
    @Expose
    private List<StateCityDetailsModel> CityList = null;

    public List<StateCityDetailsModel> getStateList() {
        return StateList;
    }

    public void setStateList(List<StateCityDetailsModel> stateList) {
        StateList = stateList;
    }

    public List<StateCityDetailsModel> getCityList() {
        return CityList;
    }

    public void setCityList(List<StateCityDetailsModel> cityList) {
        CityList = cityList;
    }
}
