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
    private String userName;
    private String profilePhoto;
    private String id;
    private String notificationContent;
    private int notificationType;
    private boolean hasUserSeen;
    private Timestamp timestamp;
    private String postId;

    public Notification(){}

    public Notification(String userId, String userName, int notificationType, String notificationContent,
                        Timestamp timestamp, String postId, String profilePhoto) {
        this.userId = userId;
        this.userName = userName;
        this.id = UUID.randomUUID().toString();
        this.notificationContent = notificationContent;
        this.notificationType = notificationType;
        this.hasUserSeen = false;
        this.timestamp = timestamp;
        this.postId = postId;
        this.profilePhoto = profilePhoto;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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

    public void setId(String notificationId) {
        this.id = notificationId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public boolean isHasUserSeen() {
        return hasUserSeen;
    }


    public String getNotificationContent() {
        return notificationContent;
    }

    public String getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }

    public int getNotificationType() {
        return notificationType;
    }


}
