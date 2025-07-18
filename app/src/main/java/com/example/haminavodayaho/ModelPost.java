package com.example.haminavodayaho;

public class ModelPost {
    int supportCount, commentCount, shareCount;
    String id, profileUrl, username, contact, timestamp, description, data;
    boolean support;
    public ModelPost(String id, String profileUrl, String username, String contact, String timestamp, String description,
                     String data, int supportCount, int commentCount, int shareCount, boolean support) {
        this.id = id;
        this.supportCount = supportCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
        this.profileUrl = profileUrl;
        this.username = username;
        this.contact = contact;
        this.timestamp = timestamp;
        this.description = description;
        this.data = data;
        this.support = support;

    }
}
