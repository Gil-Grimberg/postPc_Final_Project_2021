package com.example.centralbark_PostPc_2021;

import java.io.Serializable;
import java.util.UUID;

public class Notification implements Serializable {
    String content;
    UUID userId;
    boolean isFriendRequest;

    public Notification(UUID user_id, String content, boolean isFriendRequest)
    {
        this.content = content;
        this.userId = user_id;
        this.isFriendRequest = isFriendRequest;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFriendRequest(boolean friendRequest) {
        this.isFriendRequest = friendRequest;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean getIsFriendRequest() {
        return isFriendRequest;
    }
}
