package com.example.haminavodayaho;

public class ModelInbox {
    String id, profile, username, timestamp;
    int unreadCount;
    public ModelInbox(String id, String profile, String username, String timestamp, int unreadCount) {
        this.id = id;
        this.profile = profile;
        this.username = username;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }
}
