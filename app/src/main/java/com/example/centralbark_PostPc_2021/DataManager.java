package com.example.centralbark_PostPc_2021;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.FirebaseApp;

import java.util.UUID;

public class DataManager {

    public final Context context;
    public SharedPreferences sp;
    public FirebaseApp app;
    public FirebaseFirestore db;

    public DataManager(Context context) {
        this.context = context;
        this.sp = context.getSharedPreferences("local_db", Context.MODE_PRIVATE);
        initializeFromSp();
        app = FirebaseApp.initializeApp(this.context);
        db = FirebaseFirestore.getInstance();
    }
    private void initializeFromSp() {
        String userIdStr = this.sp.getString("userId","noId");
        UUID userId = UUID.fromString(userIdStr);

    }
    private void updateSp() {
        SharedPreferences.Editor editor = this.sp.edit();
//        editor.putString("orderId",this.myOrder.orderId);
        editor.apply();
    }
}
