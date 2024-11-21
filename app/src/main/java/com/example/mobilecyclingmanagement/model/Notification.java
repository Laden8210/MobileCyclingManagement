package com.example.mobilecyclingmanagement.model;

import com.google.firebase.Timestamp;

public class Notification {

    private String userId;
    private String message;

    private Timestamp timestamp;

    public Notification() {
    }

    public Notification(String userId, String message, Timestamp timestamp) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
