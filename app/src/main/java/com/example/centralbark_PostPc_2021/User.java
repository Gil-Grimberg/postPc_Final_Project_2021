package com.example.centralbark_PostPc_2021;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class User implements Serializable {
    UUID id;
    String username;
    String password;
    Breed breed;
    String mail;
    String photo = null;
    Date birthday;
    City city;
    Boolean rememberMe = true;
    Boolean allowNotifications = false;
    Boolean allowLocation = false;
    ArrayList<UUID> friendList;
    Set<UUID> likedUsers;
    Set<UUID> dislikeUsers;
    String selfSummary = "";

    public User()
    {
        this.id = UUID.randomUUID();
        this.friendList = new ArrayList<>();
        this.likedUsers = Collections.emptySet();
        this.dislikeUsers = Collections.emptySet();
    }

    public User(String username, String password, String mail,
                String photo, Date birthday, String breed, String city,
                boolean rememberMe, String selfSummary)
    {
        this.id = UUID.randomUUID();
        this.friendList = new ArrayList<>();
        this.likedUsers = Collections.emptySet();
        this.dislikeUsers = Collections.emptySet();
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.photo = photo;
        this.birthday = birthday;
        this.breed = Utils.convertStringToBreed(breed);
        this.city = Utils.convertStringToCity(city);
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

    public void setBreed(Breed breed) {
        this.breed = breed;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setDislikeUsers(Set<UUID> dislikeUsers) {
        this.dislikeUsers = dislikeUsers;
    }

    public void setFriendList(ArrayList<UUID> friendList) {
        this.friendList = friendList;
    }

    public void setLikedUsers(Set<UUID> likedUsers) {
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

    public Set<UUID> getDislikeUsers() {
        return dislikeUsers;
    }

    public Set<UUID> getLikedUsers() {
        return likedUsers;
    }

    public ArrayList<UUID> getFriendList() {
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

    public Breed getBreed() {
        return breed;
    }

    public City getCity() {
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

    public UUID getId() {
        return id;
    }
}

