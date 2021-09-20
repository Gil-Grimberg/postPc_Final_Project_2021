package com.example.centralbark_PostPc_2021;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerViewNotifications;
    private FirestoreRecyclerAdapter notificationsAdapter;
    private DataManager dataManager;

    public NotificationsFragment() {
        super(R.layout.fragment_notifications);
        if(dataManager == null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.recyclerViewNotifications = view.findViewById(R.id.notification_recyclerview_screen);

        Query query = this.dataManager.db.collection("Users")
                .document(this.dataManager.getMyId()).collection("Notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20);

        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class).build();

        // create adapter
        this.notificationsAdapter = new FirestoreRecyclerAdapter<Notification, RecyclerNotificationHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public RecyclerNotificationHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_one_notification, parent, false);
                return new RecyclerNotificationHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull RecyclerNotificationHolder holder, int position, @NonNull @NotNull Notification model) {
                holder.notificationContent.setText(model.getNotificationContent());
                if (!model.isHasUserSeen())
                {
                    model.setHasUserSeen(true);
                }

                String profileImgPath = model.getProfilePhoto();
                if (profileImgPath == null)
                {
                    profileImgPath = "";
                }
                if(!profileImgPath.equals("default")) {
                    StorageReference profileImag = dataManager.storage.getReference().child(profileImgPath);
                    File localProfileImFile = null;
                    try {
                        localProfileImFile = File.createTempFile("profile_photos", "g");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    File profileImFile = localProfileImFile;
                    profileImag.getFile(profileImFile)
                            .addOnSuccessListener(taskSnapshot -> holder.profilePhoto.setImageURI(Uri.fromFile(profileImFile)))
                            .addOnFailureListener(e -> {
                        // keeps the default profile image
                    });
                }
                else{
                    holder.profilePhoto.setImageResource(R.drawable.default_dog);
                }

                holder.confirmButton.setOnClickListener(v ->
                {
                    dataManager.sendNotification(NotificationTypes.FRIEND_REQUEST_ACCEPTED_NOTIFICATION,
                            model.getUserId(), null, null);
                    dataManager.addStringToUserArrayField(dataManager.getMyId(), "friendList", model.getUserId());
                    dataManager.addStringToUserArrayField(model.getUserId(), "friendList", dataManager.getMyId());
                    Utils.removeFriendsFromPosts(model.getUserId(), dataManager);
                    dataManager.removeStringFromUserArrayField(dataManager.getMyId(), "pendingRequests", model.getUserId());
                    holder.confirmButton.setVisibility(View.INVISIBLE);
                    String newText =  String.format("%s is now your friend!", model.getUserName());
                    holder.notificationContent.setText(newText);
                    model.setNotificationContent(newText);
                    dataManager.updateNotification(dataManager.getMyId(), model.getId(), model);
                    dataManager.db.collection("Users").document(model.getUserId()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot != null)
                                {
                                    User myFriend = documentSnapshot.toObject(User.class);
                                    if (myFriend != null && myFriend.getDeviceToken() != null)
                                    {
                                        dataManager.sendFirebaseNotification("Friend Request Accepted!",
                                                String.format("%s has accepted your friend request.", dataManager.getUsernameFromSp()),
                                                myFriend.getDeviceToken());
                                    }
                                }
                            }).addOnFailureListener(e -> Toast.makeText(getContext(), "DB Error", Toast.LENGTH_SHORT).show());
                    updateNotificationsIfNecessary(dataManager.getMyId(), newText, model.getUserName());
                });

                holder.notificationTime.setText(getTimeDifference(Timestamp.now(), model.getTimestamp()));
                dataManager.updateNotification(dataManager.getMyId(), model.getId(), model);
                if (model.getNotificationType() == NotificationTypes.FRIEND_REQUEST_RECEIVED_NOTIFICATION &&
                        !model.getNotificationContent().contains("is now your friend!"))
                {
                    holder.confirmButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.confirmButton.setVisibility(View.INVISIBLE);
                }
            }
        };

        this.recyclerViewNotifications.setLayoutManager(new LinearLayoutManagerWrapper(this.getContext(), RecyclerView.VERTICAL, false));
        this.recyclerViewNotifications.setAdapter(notificationsAdapter);
    }

    public void updateNotificationsIfNecessary(String userId, String newText, String requestedUserName)
    {
        dataManager.db.collection("Users").document(userId).collection("Notifications").get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots != null)
                    {
                        ArrayList<Notification> notifications = (ArrayList<Notification>) documentSnapshots.toObjects(Notification.class);
                        for (Notification notification: notifications)
                        {
                            if (notification.getNotificationType() == NotificationTypes.FRIEND_REQUEST_RECEIVED_NOTIFICATION && notification.getNotificationContent().contains(requestedUserName))
                            {
                                notification.setNotificationContent(newText);
                                dataManager.updateNotification(dataManager.getMyId(), notification.getId(), notification);
                            }
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "DB Error", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStart() {
        super.onStart();
        this.notificationsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.notificationsAdapter.stopListening();
    }

    private static class RecyclerNotificationHolder extends RecyclerView.ViewHolder
    {
        private TextView notificationTime;
        private TextView notificationContent;
        private ImageView profilePhoto;
        private Button confirmButton;

        public RecyclerNotificationHolder(View view)
        {
            super(view);
            this.notificationContent = view.findViewById(R.id.notification_message);
            this.profilePhoto = view.findViewById(R.id.profilePhoto_ImageView_one_notification_screen);
            this.confirmButton = view.findViewById(R.id.confirm_request_button_notification_screen);
            this.confirmButton.setVisibility(View.INVISIBLE);
            this.notificationTime = view.findViewById(R.id.notification_time);
        }
    }

    protected String getTimeDifference(Timestamp timestamp1, Timestamp timestamp2)
    {
        float minutesDiff = Utils.getTimestampsDifferenceInMinutes(timestamp1, timestamp2);
        if (minutesDiff < 60)
        {
            int diff = (int) minutesDiff;
            return String.format("%s minutes ago", diff);
        }

        float hoursDiff = Utils.getTimeDifferenceInHours(timestamp1, timestamp2);
        if (hoursDiff < 24)
        {
            int diff = (int) hoursDiff;
            return String.format("%s hours ago", diff);
        }

        int daysDiff = (int) Utils.getTimeDifferenceInDays(timestamp1, timestamp2);
        return String.format("%s days ago", daysDiff);
    }
}