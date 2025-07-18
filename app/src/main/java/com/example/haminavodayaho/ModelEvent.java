package com.example.haminavodayaho;

public class ModelEvent {
    String id, profile, username, contact, timestamp, data, description, registerLink;
    public ModelEvent(String id, String profile, String username, String contact, String timestamp, String data, String description, String registerLink){
        this.id = id;
        this.profile = profile;
        this.username = username;
        this.contact = contact;
        this.timestamp = timestamp;
        this.data = data;
        this.description = description;
        this.registerLink = registerLink;
    }
}
