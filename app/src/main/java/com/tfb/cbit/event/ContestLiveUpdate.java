package com.tfb.cbit.event;

public class ContestLiveUpdate {

    private String response = "";

    public ContestLiveUpdate (String response){
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
