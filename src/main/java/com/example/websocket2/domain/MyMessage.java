package com.example.websocket2.domain;

import org.springframework.context.annotation.Bean;

public class MyMessage {

    public String UserID;

    public String message;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MyMessage() {}
}
