package com.example.centralbark_PostPc_2021;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class User implements Serializable {
    private String id;
    private String username;
    private String password;
    private String breed;
    private String mail;
    private String photo = null;
    private String birthday;
    private String city;
    private Boolean rememberMe = true;
    private Boolean allowNotifications = false;
    private Boolean allowLocation = false;
    private ArrayList<String> friendList;
    private ArrayList<String> likedUsers;
    private ArrayList<String> dislikeUsers;
    private ArrayList<String> pendingRequests;
    private String selfSummary = "";
    private GeoPoint location;

    public User()
    {
        this.id = UUID.randomUUID().toString();
        this.friendList = new ArrayList<>();
        this.likedUsers = new ArrayList<>();
        this.dislikeUsers = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
        this.location = null;
    }

    public User(String username, String password, String mail,
                String photo, String birthday, String breed, String city,
                boolean rememberMe, String selfSummary, GeoPoint location)
    {
        this.id = UUID.randomUUID().toString();
        this.friendList = new ArrayList<>();
        this.likedUsers = new ArrayList<>();
        this.dislikeUsers = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.photo = photo;
        this.birthday = birthday;
        this.breed = Utils.parseBreed(breed);
        this.city = Utils.parseCity(city);
        this.rememberMe = rememberMe;
        this.selfSummary = selfSummary;
        this.location = location;
    }

    public void setAllowLocation(Boolean allowLocation) {
        this.allowLocation = allowLocation;
    }

    public void setAllowNotifications(Boolean allowNotifications) {
        this.allowNotifications = allowNotifications;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setBreed(String breed) {
        this.breed = Utils.parseBreed(breed);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = Utils.parseCity(city);
    }

    public void setDislikeUsers(ArrayList<String> dislikeUsers) {
        this.dislikeUsers = dislikeUsers;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }

    public void setLikedUsers(ArrayList<String> likedUsers) {
        this.likedUsers = likedUsers;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public void setSelfSummary(String selfSummary) {
        this.selfSummary = selfSummary;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPendingRequests(ArrayList<String> pendingRequests) { this.pendingRequests = pendingRequests; }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public ArrayList<String> getDislikeUsers() {
        return dislikeUsers;
    }

    public ArrayList<String> getLikedUsers() {
        return likedUsers;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public Boolean getAllowLocation() {
        return allowLocation;
    }

    public Boolean getAllowNotifications() {
        return allowNotifications;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public String getBreed() {
        return breed;
    }

    public String getCity() {
        return city;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoto() {
        return photo;
    }

    public String getSelfSummary() {
        return selfSummary;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getPendingRequests() { return pendingRequests; }

    public void addToLikedList(String otherId)
    {
        this.likedUsers.add(otherId);
    }
    public void addToDislikedList(String otherId)
    {
        this.dislikeUsers.add(otherId);
    }
}

