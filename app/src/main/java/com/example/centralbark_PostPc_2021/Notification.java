package com.example.centralbark_PostPc_2021;

import java.io.Serializable;
import java.util.UUID;

public class Notification implements Serializable {
    String content;
    String userId;
    boolean isFriendRequest;

    public Notification(String userId, String content, boolean isFriendRequest)
    {
        this.content = content;
        this.userId = userId;
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

    public boolean getIsFriendRequest() {
        return isFriendRequest;
    }
}
