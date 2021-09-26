package com.boss.centralbark_PostPc_2021;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DataManager {

    public final Context context;
    public SharedPreferences sp;
    public FirebaseApp app;
    public FirebaseFirestore db;
    public FirebaseStorage storage;
    private String userId;

    public DataManager(Context context) {
        this.context = context;
        this.sp = context.getSharedPreferences("local_db", Context.MODE_PRIVATE);
        this.userId = initializeFromSp();
        app = FirebaseApp.initializeApp(this.context);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void deleteUserLocationPermission()
    {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.remove("location_permission");
        editor.apply();
    }

    public void setUserLocationPermission(boolean isGranted)
    {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putBoolean("location_permission", isGranted);
        editor.apply();
    }

    public boolean isSpContainsLocationPermission()
    {
        return this.sp.contains("location_permission");
    }

    public boolean getUserLocationPermission()
    {
        return this.sp.getBoolean("location_permission", false);
    }

    public void removeDeviceTokenOnLogOut()
    {
        this.db.collection("Users").document(getMyId()).update("deviceToken", null);
    }

    public void updateDeviceToken(String token)
    {
        if (getMyId() != null)
        {
            this.db.collection("Users").document(getMyId()).update("deviceToken", token);
        }
    }

    private String initializeFromSp() {
        return this.sp.getString("userId", null);
    }

    public void updateSp(String updatedUserId) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString("userId", updatedUserId);
        editor.apply();
        this.userId = initializeFromSp();
    }

    public void updateSpForSignIn(String updatedUserId, String userMail, String userPassword) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString("userId", updatedUserId);
        editor.putString("userMail", userMail);
        editor.putString("userPassword", userPassword);
        editor.apply();
        this.userId = initializeFromSp();
    }

    public void updateSpWithUsername(String username) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString("username", username);
        editor.apply();
    }

    public String getUsernameFromSp() {
        return this.sp.getString("username", "");
    }

    public String[] getInfoForSignIn(){
    // notice - if no info, returns null
        String mail = sp.getString("userMail", null);
        String password = sp.getString("userPassword", null);
        if (mail == null || password == null) {
            return null;
        } else return new String[]{
                mail,
                password
        };
    }

    public void deleteSignInInfoFromSp() {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.remove("userMail");
        editor.remove("userPassword");
        editor.apply();
    }

    public void updateUserLocation(String userId, GeoPoint location) {
        this.db.collection("Users")
                .document(userId)
                .update("location", location);
    }

    public void addStringToUserArrayField(String userId, String fieldName, String newValue)
    {
        this.db.collection("Users").document(userId).update(fieldName, FieldValue.arrayUnion(newValue));
    }

    public void removeStringFromUserArrayField(String userId, String fieldName, String valToRemove)
    {
        this.db.collection("Users").document(userId).update(fieldName, FieldValue.arrayRemove(valToRemove));
    }

    public void addStringFromPostArrayField(String postId, String fieldName, String valToRemove)
    {
        this.db.collection("Posts").document(postId).update(fieldName, FieldValue.arrayUnion(valToRemove));
    }

    public void removeStringFromPostArrayField(String postId, String fieldName, String valToRemove)
    {
        this.db.collection("Posts").document(postId).update(fieldName, FieldValue.arrayRemove(valToRemove));
    }

    public void updateNotification(String userId, String notificationId, Notification notification)
    {
        this.db.collection("Users")
                .document(userId)
                .collection("Notifications")
                .document(notificationId)
                .set(notification);
    }

    public void addToPost(Post post) {
        // update or add post to firebase
        this.db.collection("Posts").document(post.getPostId()).set(post);
    }

    public void deletePost(Post post) {
        this.db.collection("Posts").document(post.getPostId()).delete();
    }

    public void addToUsers(User user) {
        // update or add user to firebase
        this.db.collection("Users").document(user.getId()).set(user);
    }

    public String getMyId() {
        return this.userId;
    }

    public void addNotification(String userId, Notification notification) {
        this.db.collection("Users")
                .document(userId)
                .collection("Notifications")
                .document(notification.getId())
                .set(notification);

    }

    private void removeNotification(String userId, String notificationId)
    {
        this.db.collection("Users")
                .document(userId)
                .collection("Notifications")
                .document(notificationId).delete();
    }


    public void deleteNotification(int notificationType, String friendId, String postId) {
        ArrayList<Notification> friendNotifications = new ArrayList<>();
        Task<QuerySnapshot> result = this.db.collection("Users").document(friendId).collection("Notifications").get();
        result.addOnSuccessListener(documentSnapshots -> {
            if (documentSnapshots != null && !documentSnapshots.isEmpty())
            {
                friendNotifications.addAll(documentSnapshots.toObjects(Notification.class));
                for (Notification notification: friendNotifications)
                {
                    if (notification.getNotificationType() == notificationType &&
                            notification.getUserId().equals(getMyId()))
                    {
                        if (notificationType == NotificationTypes.USER_LIKED_YOUR_POST_NOTIFICATION)
                        {
                            if (postId.equals(notification.getPostId()))
                                removeNotification(friendId, notification.getId());
                                return;
                        }

                        else if (notificationType == NotificationTypes.FRIEND_REQUEST_RECEIVED_NOTIFICATION)
                        {
                                removeNotification(friendId, notification.getId());
                        }

                    }
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(app.getApplicationContext(),
                "Error: db error. Couldn't send Notification",
                Toast.LENGTH_LONG).show());

    }

    public void sendMatchNotificationToMyself(String friendId, String friendProfilePhoto, String friendUserName)
    {
        String notificationContent = Utils.getNotificationContent(NotificationTypes.TINDER_MATCH_NOTIFICATION,
                friendUserName, null);
        Notification newNotification = new Notification(
                friendId,
                friendUserName,
                NotificationTypes.TINDER_MATCH_NOTIFICATION,
                notificationContent,
                Timestamp.now(),
                null,
                friendProfilePhoto);
        addNotification(this.getMyId(), newNotification);
    }

    public void sendNotification(int notificationType, String friendId, String postId, String park)
    {
        String notificationContent = Utils.getNotificationContent(notificationType, getUsernameFromSp(), park);
        this.db.collection("Users").document(this.getMyId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null)
                    {
                        User myUser = documentSnapshot.toObject(User.class);
                        String profilePhoto = myUser.getProfilePhoto();
                        Notification newNotification = new Notification(
                                CentralBarkApp.getInstance().getDataManager().getMyId(),
                                getUsernameFromSp(),
                                notificationType,
                                notificationContent,
                                Timestamp.now(),
                                postId,
                                profilePhoto);
                        addNotification(friendId, newNotification);
                    }
                }).addOnFailureListener(e -> Toast.makeText(app.getApplicationContext(),
                        "Error: db error. Couldn't send Notification",
                        Toast.LENGTH_LONG).show());
    }

    public void sendFirebaseNotification(String title, String body, String token)
    {
        if (token == null)
        {
            return;
        }
        Thread thread = new Thread(() -> {
            try {

                URL url = new URL ("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Authorization", "key=AAAAbzd7iSg:APA91bGPO-iIepBp-Ke-sQXX2AOAABzsXiLEbCfpg84pY39INAZhOoljFNdG5fQU1jm4ztToPYmbXX01Rd28lqD5QCgDFJPxoT4g1o1iasqwnghm1BG5h7WKam2WtPO2D-B4l3TA5F-Z");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                String content = new JSONObject()
                        .put("notification", new JSONObject().put("title", title)
                                .put("body", body)
                                .put("click_action", "OPEN_ACTIVITY_1")
                                .put("sound", "notification_sound.mp3")
                                .put("android_channel_id", "central_app_notifications")
                        )
                        .put("to", token)
                        .toString();

                try (OutputStream os = con.getOutputStream())
                {
                    byte[] input = content.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0 ,input.length);
                }
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)))
                {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null)
                    {
                        response.append(responseLine.trim());
                    }
                    Log.d("RESPONSE", response.toString());
                }
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}