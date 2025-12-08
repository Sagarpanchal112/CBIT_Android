package com.tfb.cbit.event;

public class GameAlertEvent {
    String conetestId = "";

    public GameAlertEvent(String conetestId){
        this.conetestId = conetestId;
    }

    public String getConetestId() {
        return conetestId;
    }
}
