package com.example.haminavodayaho;

public class ModelChat {
    String id, sender, receiver, textMessage, dataUrl, timestamp;
    boolean status;
    public ModelChat(String id, String sender, String receiver, String textMessage, String dataUrl, String timestamp, boolean status) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.textMessage = textMessage;
        this.dataUrl = dataUrl;
        this.timestamp = timestamp;
        this.status = status;
    }

}
