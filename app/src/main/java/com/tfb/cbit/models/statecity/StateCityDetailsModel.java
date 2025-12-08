package com.tfb.cbit.models.statecity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tfb.cbit.models.ReferralDetails.Content;

public class StateCityDetailsModel {

    @SerializedName("StateID")
    @Expose
    private int StateID;

    @SerializedName("CityID")
    @Expose
    private int CityID;

    @SerializedName("StateName")
    @Expose
    private String StateName;

    @SerializedName("CityName")
    @Expose
    private String CityName;

    public int getStateID() {
        return StateID;
    }

    public void setStateID(int stateID) {
        StateID = stateID;
    }

    public int getCityID() {
        return CityID;
    }

    public void setCityID(int cityID) {
        CityID = cityID;
    }

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }
}
