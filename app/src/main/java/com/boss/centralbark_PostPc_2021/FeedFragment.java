package com.boss.centralbark_PostPc_2021;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FeedFragment extends Fragment {
    private DataManager dataManager;
    private TextView notificationCounter;
    private View redNotificationCircle;
    private ImageView notificationButton;
    private ImageView addPostButton;
    private RecyclerView recyclerViewPosts;
    private FirestoreRecyclerAdapter postsAdapter;

    ActivityResultContracts.RequestMultiplePermissions requestMultiplePermissionsContract;
    ActivityResultLauncher<String[]> multiplePermissionActivityResultLauncher;

    public FeedFragment() {
        super(R.layout.fragment_feed);
        if(dataManager == null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // handle back pressed
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Doesn't do anything, because when back button is pressed on feed there is no where to go!!!
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);



        //first, we activate the location service, so we can update the user location in the db:
        //to do that, we first need to ask location permissions from the user:
        requestMultiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionActivityResultLauncher = registerForActivityResult(requestMultiplePermissionsContract, isGranted ->
        {
            Log.d("PERMISSIONS", "Launcher result: " + isGranted.toString());
            if (isGranted.containsValue(false)) {
                Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
                multiplePermissionActivityResultLauncher.launch(Utils.PERMISSIONS);
            }
        });
        askPermissions(); // this will cause a loop until the user agrees :)

        // now we can start the location service:
        ((MainActivity) requireActivity()).startLocationService();


        // Inflate the layout for this fragment
        this.notificationButton = view.findViewById(R.id.notification_button_feed_screen);
        this.recyclerViewPosts = view.findViewById(R.id.post_recyclerview_feed_screen);
        this.addPostButton = view.findViewById(R.id.add_post_button_feed_screen);
        this.notificationCounter = view.findViewById(R.id.notification_counter);
        this.redNotificationCircle = view.findViewById(R.id.red_circle_feed_screen);

        this.redNotificationCircle.setVisibility(View.GONE); // don't show the notification circle unless there is a notification

        // query relevant posts:
        Query query = this.dataManager.db.collection("Posts")
                .whereArrayContains("friendList",this.dataManager.getMyId())
                .orderBy("timePosted", Query.Direction.DESCENDING)
                .limit(10);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class).build();

        // create adapter
        this.postsAdapter = new FirestoreRecyclerAdapter<Post,RecyclerPostsHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public RecyclerPostsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_one_post_for_feed, parent, false);
                return new RecyclerPostsHolder(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull @NotNull RecyclerPostsHolder holder, int position, @NonNull @NotNull Post model) {
                // set the user name
                holder.userNameTitle.setText(model.getUserName());
                holder.userNameContent.setText(model.getUserName());

                // set the content of the post
                holder.postContent.setText(model.getContent());

                // set the delete button
                if(!model.getUserId().equals(dataManager.getMyId())){
                    holder.deletePost.setVisibility(View.GONE);
                }

                holder.deletePost.setOnClickListener(v -> dataManager.deletePost(model));

                // set the number of likes, and update
                holder.numOfLikes.setText(String.valueOf(model.getNumOfLikes()));
                holder.likeButton.setOnClickListener(v -> {
                    if(model.isUserLikesPost(dataManager.getMyId())){
                        model.removeLike(dataManager.getMyId());
                        dataManager.deleteNotification(NotificationTypes.USER_LIKED_YOUR_POST_NOTIFICATION, model.getUserId(), model.getPostId());
                    }
                    else{
                        model.addLike(dataManager.getMyId());
                        if (!dataManager.getMyId().equals(model.getUserId()))
                        {
                            dataManager.sendNotification(NotificationTypes.USER_LIKED_YOUR_POST_NOTIFICATION, model.getUserId(), model.getPostId(), "");
                            dataManager.db.collection("Users").document(model.getUserId()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot != null)
                                        {
                                            User myFriend = documentSnapshot.toObject(User.class);
                                            if (myFriend != null && myFriend.getDeviceToken() != null)
                                            {
                                                dataManager.sendFirebaseNotification("Someone Liked Your Post!",
                                                        String.format("%s likes your post", dataManager.getUsernameFromSp()),
                                                        myFriend.getDeviceToken());
                                            }
                                        }
                                    }).addOnFailureListener(e -> Toast.makeText(getContext(), "DB Error", Toast.LENGTH_SHORT).show());
                        }
                    }
                    holder.numOfLikes.setText(String.valueOf(model.getNumOfLikes()));
                });

                // set the time of the post
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String curDateString = sdf.format(new Date());
                try {
                    Date curTime = sdf.parse (curDateString);
                    Date postTime = model.parseStringToDate();
                    assert curTime != null;
                    long diffInMilli = curTime.getTime() - postTime.getTime();
                    long diffInHours = TimeUnit.HOURS.convert(diffInMilli, TimeUnit.MILLISECONDS);

                    if (diffInHours < 1)
                    {
                        long diffInMin = TimeUnit.MINUTES.convert(diffInMilli, TimeUnit.MILLISECONDS);
                        holder.postTime.setText(diffInMin + " minutes ago");
                    }

                    else if (diffInHours < 24)
                    {
                        holder.postTime.setText(diffInHours + " hours ago");
                    }

                    else
                    {
                        long diffInDays = TimeUnit.DAYS.convert(diffInMilli, TimeUnit.MILLISECONDS);
                        holder.postTime.setText(diffInDays + " days ago");
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // set users profile image
                if(!model.getUserProfilePhoto().equals("default")){
                    StorageReference profileImag = dataManager.storage.getReference().child(model.getUserProfilePhoto());
                    File localProfileImFile = null;
                    try {
                        localProfileImFile = File.createTempFile("profile_photos", "g");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File profileImFile = localProfileImFile;
                    profileImag.getFile(profileImFile).addOnSuccessListener(taskSnapshot ->
                            holder.profileIm.setImageURI(Uri.fromFile(profileImFile))).addOnFailureListener(exception -> {
                                // keeps the default profile image
                            });
                }
                else{
                    holder.profileIm.setImageResource(R.drawable.default_dog);
                }


                // set users post image
                if(!model.getUploadedPhoto().equals("default")){
                    StorageReference postImag = dataManager.storage.getReference().child(model.getUploadedPhoto());
                    File localPostImFile = null;
                    try {
                        localPostImFile = File.createTempFile("post_photos", "g");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File finalLocalPostImFile = localPostImFile;
                    postImag.getFile(localPostImFile).addOnSuccessListener(taskSnapshot ->
                            holder.postIm.setImageURI(Uri.fromFile(finalLocalPostImFile))).addOnFailureListener(exception -> {});

                    holder.profileIm.setOnClickListener(v -> {
                        Utils.moveBetweenFragments(R.id.the_screen, new myProfileFragment(model.getUserId()), getActivity(), "myProfile");
                    });

                    holder.userNameTitle.setOnClickListener(v -> {
                        Utils.moveBetweenFragments(R.id.the_screen, new myProfileFragment(model.getUserId()), getActivity(), "myProfile");
                    });
                }
                else{
                    holder.postIm.setImageResource(R.drawable.default_dog);
                }
            }
        };

        this.recyclerViewPosts.setLayoutManager(new LinearLayoutManagerWrapper(this.getContext(),RecyclerView.VERTICAL,false));
        this.recyclerViewPosts.setAdapter(postsAdapter);

        this.dataManager.db.collection("Users").document(dataManager.getMyId()).collection("Notifications").get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots != null && !documentSnapshots.isEmpty())
                    {
                        int numberOfNotifications = 0;
                        ArrayList<Notification> notificationList = new ArrayList<>(documentSnapshots.toObjects(Notification.class));
                        for (Notification notification: notificationList)
                        {
                            if (!notification.isHasUserSeen())
                            {
                                numberOfNotifications++;
                            }
                        }
                        if (numberOfNotifications > 0)
                        {
                            notificationCounter.setText(String.valueOf(numberOfNotifications));
                            redNotificationCircle.setVisibility(View.VISIBLE);
                        }
                    }
                });

        // move to add post screen
        this.addPostButton.setOnClickListener(v -> {
            Utils.moveBetweenFragments(R.id.the_screen, new AddPostFragment(), getActivity(), "add_post");
        });

        this.notificationButton.setOnClickListener(v ->
        {
            Utils.moveBetweenFragments(R.id.the_screen, new NotificationsFragment(), getActivity(), "notification_activity");
        });
    }

    private void askPermissions() {
        if (!hasPermissions(Utils.PERMISSIONS))
        {
            multiplePermissionActivityResultLauncher.launch(Utils.PERMISSIONS);
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.postsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.postsAdapter.stopListening();
    }



    // the view holder for the adapter
    private class RecyclerPostsHolder extends RecyclerView.ViewHolder {
        private ImageView profileIm;
        private TextView userNameTitle;
        private ImageView postIm;
        private ImageView likeButton;
        private TextView numOfLikes;
        private TextView postTime;
        private TextView userNameContent;
        private TextView postContent;
        private ImageView deletePost;

        public RecyclerPostsHolder(View view) {
            super(view);
            this.profileIm = view.findViewById(R.id.profile_image_one_post_feed_screen);
            this.userNameTitle = view.findViewById(R.id.user_name_title_textview_one_post_feed_screen);
            this.postIm = view.findViewById(R.id.post_image_one_post_feed_screen);
            this.likeButton = view.findViewById(R.id.like_button_one_post_feed_screen);
            this.numOfLikes = view.findViewById(R.id.num_of_likes_textview_one_post_feed_screen);
            this.postTime = view.findViewById(R.id.post_time_textview_one_post_feed_screen);
            this.userNameContent = view.findViewById(R.id.user_name_content_textview_one_post_feed_screen);
            this.postContent = view.findViewById(R.id.content_textview_one_post_feed_screen);
            this.deletePost = view.findViewById(R.id.delete_post_icon_one_post_feed_screen);
        }
    }
}

