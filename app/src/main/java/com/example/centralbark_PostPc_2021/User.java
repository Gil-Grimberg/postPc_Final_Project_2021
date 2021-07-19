package com.example.centralbark_PostPc_2021;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class User implements Serializable {
    String id;
    String username;
    String password;
    String breed;
    String mail;
    String photo = null;
    Date birthday;
    String city;
    Boolean rememberMe = true;
    Boolean allowNotifications = false;
    Boolean allowLocation = false;
    ArrayList<String> friendList;
    Set<String> likedUsers;
    Set<String> dislikeUsers;
    String selfSummary = "";

    public User()
    {
        this.id = UUID.randomUUID().toString();
        this.friendList = new ArrayList<>();
        this.likedUsers = Collections.emptySet();
        this.dislikeUsers = Collections.emptySet();
    }

    public User(String username, String password, String mail,
                String photo, Date birthday, String breed, String city,
                boolean rememberMe, String selfSummary)
    {
        this.id = UUID.randomUUID().toString();
        this.friendList = new ArrayList<>();
        this.likedUsers = Collections.emptySet();
        this.dislikeUsers = Collections.emptySet();
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.photo = photo;
        this.birthday = birthday;
        this.breed = Utils.parseBreed(breed);
        this.city = Utils.parseCity(city);
        this.rememberMe = rememberMe;
        this.selfSummary = selfSummary;
    }

    public void setAllowLocation(Boolean allowLocation) {
        this.allowLocation = allowLocation;
    }

    public void setAllowNotifications(Boolean allowNotifications) {
        this.allowNotifications = allowNotifications;
    }

    public void setBirthday(Date birthday) {
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

    public void setDislikeUsers(Set<String> dislikeUsers) {
        this.dislikeUsers = dislikeUsers;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }

    public void setLikedUsers(Set<String> likedUsers) {
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

    public Set<String> getDislikeUsers() {
        return dislikeUsers;
    }

    public Set<String> getLikedUsers() {
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

    public Date getBirthday() {
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
}

