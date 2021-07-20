package com.example.centralbark_PostPc_2021;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Post implements Serializable {

    private String userId;
    private String postId;
    private String username;
    private String userProfilePhoto;
    private String uploadedPhoto;
    private String content;
    private Integer numOfLikes;
    private String timePosted;

    public Post(){}

    public Post(String userId, String username, String userProfilePhoto, String uploadedPhoto,String content, Integer numOfLikes, String timePosted){
        this.userId = userId;
        this.postId = UUID.randomUUID().toString();
        this.username = username;
        this.userProfilePhoto = userProfilePhoto;
        this.uploadedPhoto = uploadedPhoto;
        this.content = content;
        this.numOfLikes = numOfLikes;
        this.timePosted = timePosted; // todo: when creating new post need to send SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public String getTimePosted() {
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

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
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

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }

    public void setUploadedPhoto(String uploadedPhoto) {
        this.uploadedPhoto = uploadedPhoto;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserProfilePhoto(String userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }
}

//class SortPosts implements Comparator<Post> {
//    public int compare(Post post1, Post post2){
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        Date post1date = format.parse(post1.getTimePosted());
//        if(post1.getTimePosted()) //todo: filter by time need to parse
//    }
//}
