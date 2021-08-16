package com.example.centralbark_PostPc_2021;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.UUID;


final class NotificationTypes {
    static final int FRIEND_REQUEST_RECEIVED_NOTIFICATION = 0;
    static final int FRIEND_REQUEST_ACCEPTED_NOTIFICATION = 1;
    static final int USER_LIKED_YOUR_POST_NOTIFICATION = 2;
    static final int TINDER_MATCH_NOTIFICATION = 3;
    static final int USER_AT_THE_DOG_PARK_NOTIFICATION = 4;
}


public class Notification implements Serializable {

    private String userId; // the user that creates the notification
    private String notificationId;
    private String notificationContent;
    private int notificationType;
    private boolean isFriendRequest;
    private boolean hasUserSeen;
    private Timestamp timestamp;

    public Notification(){}

    public Notification(String userId, int notificationType, String notificationContent,
                        boolean isFriendRequest, Timestamp timestamp) {
        this.userId = userId;
        this.notificationId = UUID.randomUUID().toString();
        this.notificationContent = notificationContent;
        this.isFriendRequest = isFriendRequest;
        this.notificationType = notificationType;
        this.hasUserSeen = false;
        this.timestamp = timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setFriendRequest(boolean friendRequest) {
        this.isFriendRequest = friendRequest;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setHasUserSeen(boolean hasUserSeen) {
        this.hasUserSeen = hasUserSeen;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getNotificationContent() {
        return notificationContent;
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

    public int getNotificationType() {
        return notificationType;
    }


}
