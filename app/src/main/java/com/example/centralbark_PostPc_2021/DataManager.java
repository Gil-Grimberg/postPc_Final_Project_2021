package com.example.centralbark_PostPc_2021;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.UUID;

public class DataManager {

    public final Context context;
    public SharedPreferences sp;
    public FirebaseApp app;
    public FirebaseFirestore db;
    //todo: add the storage object of firestore
    private String userId;
    public DataManager(Context context) {
        this.context = context;
        this.sp = context.getSharedPreferences("local_db", Context.MODE_PRIVATE);
        this.userId = initializeFromSp();
        if (this.userId.equals("noId"))
        {
            //todo: go to main sign Up/In screen!
        }
        app = FirebaseApp.initializeApp(this.context);
        db = FirebaseFirestore.getInstance();
    }
    private String initializeFromSp(){
        return this.sp.getString("userId","noId");

    }
    private void updateSp() {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString("userId",userId);
        editor.apply();
    }

    //todo: delete from sp? from db?

    public void addToPost(Post post){}

    public void addToUsers(User user){}

    public void addToNotifications(Notification notification){}

    public ArrayList<Post> getPosts(){return new ArrayList<>();}//todo: return the list of posts related to the user

    public ArrayList<Notification> getNotifications(){return new ArrayList<>();}//todo: return the list of posts related to the user





}
