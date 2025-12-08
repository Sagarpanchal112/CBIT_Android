package com.tfb.cbit.event;

public class SocketConnectionEvent {
    String message = "";

    public SocketConnectionEvent(String message){
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
