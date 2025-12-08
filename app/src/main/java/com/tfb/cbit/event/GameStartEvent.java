package com.tfb.cbit.event;

public class GameStartEvent {
    String response = "";

    public GameStartEvent(String response){
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
