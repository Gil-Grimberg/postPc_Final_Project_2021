package com.example.centralbark_PostPc_2021;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

public class Post implements Serializable {

    private String userId;
    private String postId;
    private String userName;
    private String uploadedPhoto;
    private String userProfilePhoto;
    private String content;
    private Integer numOfLikes;
    private ArrayList<String> usersLikesLst;
    private String timePosted;
    private ArrayList<String> friendList;
    private DataManager dataManager;

    public Post(){}

    public Post(String userId, String postId, String userName, String userProfilePhoto, String uploadedPhoto, String content, Integer numOfLikes, String timePosted, ArrayList<String> friendList){
        this.userId = userId;
        this.postId = postId;
        this.userName = userName;
        this.uploadedPhoto = uploadedPhoto;
        this.userProfilePhoto = userProfilePhoto;
        this.content = content;
        this.numOfLikes = numOfLikes;
        this.usersLikesLst = new ArrayList<>();
        this.timePosted = timePosted; // todo: when creating new post need to send SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.friendList = new ArrayList<>();
        this.friendList.addAll(friendList);
        this.friendList.add(this.userId);
        this.dataManager = CentralBarkApp.getInstance().getDataManager();
    }

    public String getContent() {
        return content;
    }

    public String getUserName() {
        return userName;
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

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public ArrayList<String> getUsersLikesLst() {
        return usersLikesLst;
    }

    public String getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public void setUsersLikesLst(ArrayList<String> usersLikesLst) {
        this.usersLikesLst = usersLikesLst;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }

    public void setUserProfilePhoto(String userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }

    public boolean isUserLikesPost(String userId){
        if(this.usersLikesLst==null){ // todo: maybe delete, created this patch because i created a post throw the db
            this.usersLikesLst = new ArrayList<>();
            return false;
        }

        for(String id: this.usersLikesLst){
            if (id.equals(userId)){
                return true;
            }
        }
        return false;
    }

    public void addLike(String userId){
        this.numOfLikes+=1;
        this.usersLikesLst.add(userId);
        this.dataManager.addToPost(this);
    }

    public void removeLike(String userId){
        this.numOfLikes-=1;
        this.usersLikesLst.remove(userId);
        this.dataManager.addToPost(this);
    }
    public Date parseStringToDate() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(this.timePosted);
    }
}

//class SortPosts implements Comparator<Post> {
//    public int compare(Post post1, Post post2){
//        try {
//            Date post1date = post1.parseStringToDate();
//            Date post2date = post2.parseStringToDate();
//            if(post1date.before(post2date)){
//                return -1;
//            }
//            if(post1date.after(post2date)){
//                return 1;
//            }
//            else{
//                return 0;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return 0; // default value for a case we cant parse the date
//
//    }
//}
