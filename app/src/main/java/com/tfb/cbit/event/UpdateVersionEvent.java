package com.tfb.cbit.event;

public class UpdateVersionEvent {
    String message = "";

    public UpdateVersionEvent(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
