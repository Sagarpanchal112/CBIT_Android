package com.tfb.cbit.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnyTimeGameContestList implements Serializable {
    public  int statusCode;
    public String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public List<Content> content=new ArrayList<>();
    public class Content implements Serializable{
        public int contestID;
        public String game_type;
        public String gameMode;

        public int getContestID() {
            return contestID;
        }

        public void setContestID(int contestID) {
            this.contestID = contestID;
        }

        public String getGame_type() {
            return game_type;
        }

        public void setGame_type(String game_type) {
            this.game_type = game_type;
        }

        public String getGameMode() {
            return gameMode;
        }

        public void setGameMode(String gameMode) {
            this.gameMode = gameMode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String name;

    }
}
