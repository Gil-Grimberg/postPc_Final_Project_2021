package com.example.centralbark_PostPc_2021;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class DataManager {

    public final Context context;
    public SharedPreferences sp;
    public FirebaseApp app; //
    public FirebaseFirestore db;
    public FirebaseStorage storage;
    private String separator = "7f802626-2d3c-4b94-8df3-a5bb13fc6ff9";
    private String userId;

    public DataManager(Context context) {
        this.context = context;
        this.sp = context.getSharedPreferences("local_db", Context.MODE_PRIVATE);
        this.userId = initializeFromSp();
//        if (this.userId.equals("noId")) {
//            //todo: go to main sign Up/In screen!
//        }
        app = FirebaseApp.initializeApp(this.context);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
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

    public void deleteUsernameFromSp() {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.remove("username");
        editor.apply();
    }

    public String[] getInfoForSignIn()
    // notice - if no info, returns null
    {
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

    public void removeStringFromUserArrayField(String userId, String fieldName, String newValue)
    {
        this.db.collection("Users").document(userId).update(fieldName, FieldValue.arrayRemove(newValue));
    }

    public void updateNotification(String userId, String notificationId, Notification notification)
    {
        this.db.collection("Users")
                .document(userId)
                .collection("Notifications")
                .document(notificationId)
                .set(notification);
    }


    public String uploadImgToStorageAndGetImgPath(String localImgPath, String RemoteImageName) {
        StorageReference storageReference = storage.getReference();
        StorageReference imgRef = storageReference.child(RemoteImageName);
        UploadTask uploadTask = imgRef.putFile(Uri.fromFile(new File(localImgPath)));
        final String[] downloadUrl = new String[1];
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { //todo: check that it works
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl[0] = taskSnapshot.getStorage().getDownloadUrl().toString();
            }
        });

        return downloadUrl[0];
    }

    public void addToPost(Post post) {
        // update or add post to firebase
        this.db.collection("Posts").document(post.getPostId()).set(post);
    }

    public void addToUsers(User user) {
        // update or add user to firebase
        this.db.collection("Users").document(user.getId()).set(user);
    }


    public ArrayList<Post> getPostsById(String idToFindPostsFor) {
        ArrayList<Post> myPosts = new ArrayList<>();
        this.db.collection("Users").document(idToFindPostsFor).collection("myPosts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Post obj = document.toObject(Post.class);
                        myPosts.add(obj);
                    }
                }
            }
        });
        return myPosts;
    }

    public User getUserById(String idToFind) { // todo: check if works
        User[] user = new User[1];
        this.db.collection("Users").document(idToFind).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user[0] = documentSnapshot.toObject(User.class);
            }
        });
        return user[0];
    }


    /**
     * @param post
     * @return a string representing the post with $ as seperators
     */
    public String convertPostToString(Post post) { //todo: probably unnecessary
        return post.getUserId() + separator + post.getPostId() + separator + post.getUserName() + separator +
                post.getFriendList() + post.getUsersLikesLst() + separator + post.getUploadedPhoto() + separator +
                post.getContent() + separator + post.getNumOfLikes().toString() + separator + post.getTimePosted().toString();
    }

    /**
     * @param user
     * @return 4 strings in an array. the first one is for all fields that are not lists or sets
     * the second is for friendsList, third for liked and fourth for dislike
     */
    public String[] convertUserToString(User user) { //todo: probably unnecessary
        String fields = user.getId() + separator + user.getUsername() + separator + user.getPassword() + separator + user.getBreed() + separator + user.getMail() + separator + user.getProfilePhoto() + separator + user.getBirthday().toString() + separator + user.getCity() + separator + user.getRememberMe().toString() + separator + user.getAllowNotifications().toString() + separator + user.getAllowLocation().toString() + separator + user.getSelfSummary();
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

    private String likedAndDislikedUsersToStr(ArrayList<String> users) {
        String res = "";
        for (String user : users) {
            res += user + separator;
        }
        res = res.substring(0, res.length() - 2);
        return res;
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
        result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
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
                                if (!notification.isHasUserSeen())
                                {
                                    removeNotification(friendId, notification.getId());
                                    return;
                                }
                            }

                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(app.getApplicationContext(),
                        "Error: db error. Couldn't send Notification",
                        Toast.LENGTH_LONG).show();

            }
        });

    }

    public void sendNotification(int notificationType, String friendId, String postId, String park)
    {
        String notificationContent = Utils.getNotificationContent(notificationType, getUsernameFromSp(), park);

        this.db.collection("Users").document(this.getMyId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(app.getApplicationContext(),
                        "Error: db error. Couldn't send Notification",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}