package com.example.centralbark_PostPc_2021;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class DataManager {

    public final Context context;
    public SharedPreferences sp;
    public FirebaseApp app; //
    public FirebaseFirestore db;
    private String separator = "7f802626-2d3c-4b94-8df3-a5bb13fc6ff9";
    //todo: add the storage object of firestore
    private String userId;

    public DataManager(Context context) {
        this.context = context;
        this.sp = context.getSharedPreferences("local_db", Context.MODE_PRIVATE);
        this.userId = initializeFromSp();
        if (this.userId.equals("noId")) {
            //todo: go to main sign Up/In screen!
        }
        app = FirebaseApp.initializeApp(this.context);
        db = FirebaseFirestore.getInstance();
    }

    private String initializeFromSp() {
        return this.sp.getString("userId", "noId");

    }

    private void updateSp() {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString("userId", userId);
        editor.apply();
    }

    //todo: delete from sp? from db?

    public void addToPost(Post post) {
        this.db.collection("Users").document(this.userId).collection("myPosts").document(post.getPostId()).set(post);
    }

    public void addToUsers(User user) {
        this.db.collection("Users").document(user.getId()).set(user);
    }

    public void addToNotifications(Notification notification) {
        this.db.collection("Users").document(this.userId).collection("myNotifications").document(notification.getNotificationId()).set(notification);

    }

    public ArrayList<Post> getPosts() {
        ArrayList<Post> myPosts = new ArrayList<>();
        this.db.collection("Users").document(this.userId).collection("myPosts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        myPosts.add(document.toObject(Post.class));
                    }
                }
            }
        });

        return myPosts;
    }

    public ArrayList<Notification> getNotifications() {
        ArrayList<Notification> myNotifications = new ArrayList<>();
        this.db.collection("Users").document(this.userId).collection("myNotifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        myNotifications.add(document.toObject(Notification.class));
                    }
                }
            }
        });

        return myNotifications;
    }
    /**
     * @param post
     * @return a string representing the post with $ as seperators
     */
    public String convertPostToString(Post post) { //todo: probably unnecessary
        return post.getUserId() + separator + post.getPostId() + separator + post.getUsername() + separator +
                post.getUserProfilePhoto() + separator + post.getUploadedPhoto() + separator +
                post.getContent() + separator + post.getNumOfLikes().toString() + separator + post.getTimePosted().toString();
    }

    /**
     * @param user
     * @return 4 strings in an array. the first one is for all fields that are not lists or sets
     * the second is for friendsList, third for liked and fourth for dislike
     */
    public String[] convertUserToString(User user) { //todo: probably unnecessary
        String fields = user.getId() + separator + user.getUsername() + separator + user.getPassword() + separator + user.getBreed() + separator + user.getMail() + separator + user.getPhoto() + separator + user.getBirthday().toString() + separator + user.getCity() + separator + user.getRememberMe().toString() + separator + user.getAllowNotifications().toString() + separator + user.getAllowLocation().toString() + separator + user.getSelfSummary();
        String friendsListStr = friendsListToStr(user.getFriendList());
        String likedUsersStr = likedAndDislikedUsersToStr(user.getLikedUsers());
        String dislikeUsersStr = likedAndDislikedUsersToStr(user.getDislikeUsers());
        return new String[]{fields, friendsListStr, likedUsersStr, dislikeUsersStr};
    }

    public String convertNotificationToString(Notification notification) { //todo: probably unnecessary
        return "";
    }

    private String friendsListToStr(ArrayList<String> friendsList) {
        String res = "";
        for (String user : friendsList) {
            res += user + separator;
        }
        res = res.substring(0, res.length() - 2);
        return res;
    }

    private String likedAndDislikedUsersToStr(Set<String> users) {
        String res = "";
        for (String user : users) {
            res += user + separator;
        }
        res = res.substring(0, res.length() - 2);
        return res;
    }


}



