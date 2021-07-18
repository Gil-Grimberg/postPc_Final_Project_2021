package com.example.centralbark_PostPc_2021;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class User {
    UUID id;
    String username;
    String password;
    Breed breed;
    String mail;
    String photo = null;
    Date birthday;
    City city;
    Boolean remember_me = true;
    Boolean allow_notifications = false;
    Boolean allow_location = false;
    ArrayList<UUID> friend_list;
    Set<UUID> liked_users;
    Set<UUID> dislike_users;
    String self_summary = "";

    public User()
    {
        this.id = UUID.randomUUID();
        this.friend_list = new ArrayList<>();
        this.liked_users = Collections.emptySet();
        this.dislike_users = Collections.emptySet();
    }

    public User(String username, String password, String mail,
                String photo, Date birthday, String breed, String city,
                boolean remember_me, String self_summary)
    {
        this.id = UUID.randomUUID();
        this.friend_list = new ArrayList<>();
        this.liked_users = Collections.emptySet();
        this.dislike_users = Collections.emptySet();
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.photo = photo;
        this.birthday = birthday;
        this.breed = Utils.convertStringToBreed(breed);
        this.city = Utils.convertStringToCity(city);
        this.remember_me = remember_me;
        this.self_summary = self_summary;
    }

    public void setAllow_location(Boolean allow_location) {
        this.allow_location = allow_location;
    }

    public void setAllow_notifications(Boolean allow_notifications) {
        this.allow_notifications = allow_notifications;
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

    public void setDislike_users(Set<UUID> dislike_users) {
        this.dislike_users = dislike_users;
    }

    public void setFriend_list(ArrayList<UUID> friend_list) {
        this.friend_list = friend_list;
    }

    public void setLiked_users(Set<UUID> liked_users) {
        this.liked_users = liked_users;
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

    public void setRemember_me(Boolean remember_me) {
        this.remember_me = remember_me;
    }

    public void setSelf_summary(String self_summary) {
        this.self_summary = self_summary;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<UUID> getDislike_users() {
        return dislike_users;
    }

    public Set<UUID> getLiked_users() {
        return liked_users;
    }

    public ArrayList<UUID> getFriend_list() {
        return friend_list;
    }

    public Boolean getAllow_location() {
        return allow_location;
    }

    public Boolean getAllow_notifications() {
        return allow_notifications;
    }

    public Boolean getRemember_me() {
        return remember_me;
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

    public String getSelf_summary() {
        return self_summary;
    }

    public String getUsername() {
        return username;
    }

    public UUID getId() {
        return id;
    }
}

