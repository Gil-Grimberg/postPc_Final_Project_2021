package com.example.centralbark_PostPc_2021;

import java.io.Serializable;
import java.util.UUID;

public class Notification implements Serializable {
    String userId;
    String notificationId;
    String content;
    boolean isFriendRequest;


    public Notification(String userId, String content, boolean isFriendRequest) {
        this.userId = userId;
        this.notificationId = UUID.randomUUID().toString();
        this.content = content;
        this.isFriendRequest = isFriendRequest;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFriendRequest(boolean friendRequest) {
        this.isFriendRequest = friendRequest;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public String getUserId() {
        return userId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public boolean getIsFriendRequest() {
        return isFriendRequest;
    }
}
