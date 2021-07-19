package com.example.centralbark_PostPc_2021;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Post implements Serializable {

    UUID userId;
    UUID postId;
    String username;
    String userProfilePhoto;
    String uploadedPhoto;
    String content;
    Integer numOfLikes;
    Date timePosted;

    public Post(UUID userId, String username, String userProfilePhoto, String uploadedPhoto,String content, Integer numOfLikes, Date timePosted){
        this.userId = userId;
        this.postId = UUID.randomUUID();
        this.username = username;
        this.userProfilePhoto = userProfilePhoto;
        this.uploadedPhoto = uploadedPhoto;
        this.content = content;
        this.numOfLikes = numOfLikes;
        this.timePosted = timePosted;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public Date getTimePosted() {
        return timePosted;
    }

    public Integer getNumOfLikes() {
        return numOfLikes;
    }

    public String getUploadedPhoto() {
        return uploadedPhoto;
    }

    public String getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public UUID getPostId() {
        return postId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNumOfLikes(Integer numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public void setTimePosted(Date timePosted) {
        this.timePosted = timePosted;
    }

    public void setUploadedPhoto(String uploadedPhoto) {
        this.uploadedPhoto = uploadedPhoto;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setUserProfilePhoto(String userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }
}
