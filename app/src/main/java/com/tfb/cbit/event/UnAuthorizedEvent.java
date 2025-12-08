package com.tfb.cbit.event;

public class UnAuthorizedEvent {
    String message = "";

    public UnAuthorizedEvent(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
