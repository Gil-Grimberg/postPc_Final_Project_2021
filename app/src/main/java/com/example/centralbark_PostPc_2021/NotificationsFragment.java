package com.example.centralbark_PostPc_2021;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
                if (model.getNotificationType() == NotificationTypes.FRIEND_REQUEST_RECEIVED_NOTIFICATION &&
                    model.getNotificationContent().contains("wants"))
                {
                    holder.confirmButton.setVisibility(View.VISIBLE);
                }

                String profileImgPath = model.getProfilePhoto();
                if (profileImgPath == null)
                {
                    profileImgPath = "";
                }
                StorageReference profileImag = dataManager.storage.getReference().child(profileImgPath);
                File localProfileImFile = null;
                try {
                    localProfileImFile = File.createTempFile("profile_photos", "g");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File profileImFile = localProfileImFile;
                profileImag.getFile(profileImFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        holder.profilePhoto.setImageURI(Uri.fromFile(profileImFile));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        // keeps the default profile image
                    }
                });

                holder.confirmButton.setOnClickListener(v ->
                {
                    dataManager.sendNotification(NotificationTypes.FRIEND_REQUEST_ACCEPTED_NOTIFICATION,
                            model.getUserId(), null, null);
                    dataManager.addStringToUserArrayField(dataManager.getMyId(), "friendList", model.getUserId());
                    dataManager.addStringToUserArrayField(model.getUserId(), "friendList", dataManager.getMyId());
                    dataManager.removeStringFromUserArrayField(dataManager.getMyId(), "pendingRequests", model.getUserId());
                    holder.confirmButton.setVisibility(View.INVISIBLE);
                    String newText =  String.format("%s is now your friend!", model.getUserName());
                    holder.notificationContent.setText(newText);
                    dataManager.removeStringFromUserArrayField(dataManager.getMyId(), "pendingRequests", model.getUserId());
                    model.setNotificationContent(newText);
                    dataManager.updateNotification(dataManager.getMyId(), model.getId(), model);
                });

            }
        };

        this.recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        this.recyclerViewNotifications.setAdapter(notificationsAdapter);


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

    private class RecyclerNotificationHolder extends RecyclerView.ViewHolder
    {
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
        }
    }
}