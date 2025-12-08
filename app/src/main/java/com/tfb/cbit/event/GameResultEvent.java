package com.tfb.cbit.event;

public class GameResultEvent {
    String response = "";

    public GameResultEvent(String response){
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
