package com.example.centralbark_PostPc_2021;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class myProfileFragment extends Fragment {

    private DataManager dataManager;
    private ImageView profileIm;
    private TextView userName;
    private TextView city;
    private TextView breed;
    private TextView age;
    private RecyclerView friendsRecycler;
    private RecyclerView postsRecycler;
    private FirestoreRecyclerAdapter postsAdapter;
    private FirestoreRecyclerAdapter friendsAdapter;
    private ImageView settingsButton;
    private Button makeFriendButton;
    private Button unfriendButton;
    private String curUserId;
    private boolean anotherUser;

    public myProfileFragment(String curUserId) {
        super(R.layout.fragment_my_profile);
        if(dataManager ==null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
        this.curUserId = curUserId;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);

        this.profileIm = view.findViewById(R.id.profile_image_profile_screen);
        this.userName = view.findViewById(R.id.user_name_textview_profile_screen);
        this.city = view.findViewById(R.id.lives_in_profile_screen);
        this.breed = view.findViewById(R.id.breed_profile_screen);
        this.age = view.findViewById(R.id.age_profile_screen);
        this.friendsRecycler = view.findViewById(R.id.friends_list_recycler_profile_screen);
        this.postsRecycler = view.findViewById(R.id.post_list_recycler_profile_screen);
        this.settingsButton = view.findViewById(R.id.settings_button_profile_screen);
        this.makeFriendButton = view.findViewById(R.id.makeFriend_Button_profile_screen);
        this.unfriendButton = view.findViewById(R.id.unFriend_Button_profile_screen);

        // check if this is my profile or another user's profile
        if(this.curUserId.equals(this.dataManager.getMyId())){ // case this is my profile
            this.makeFriendButton.setVisibility(View.GONE);
            this.unfriendButton.setVisibility(View.GONE);
            this.anotherUser = false;
        }
        else{ // case it is another users profile
            this.settingsButton.setVisibility(View.GONE);
            this.anotherUser = true;
        }

        // Move to settings
        this.settingsButton.setOnClickListener(v->{
            Utils.moveBetweenFragments(R.id.the_screen, new settingsFragment(), getActivity(), "settings");
        });

        // Access user
        this.dataManager.db.collection("Users").document(this.curUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User curUser = documentSnapshot.toObject(User.class);
                // set user name
                userName.setText(curUser.getUsername());

                // set city
                city.setText(curUser.getCity());

                // set breed
                breed.setText(curUser.getBreed());

                //set Age if less than 1 write "Puppy"
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String curDateString = sdf.format(new Date());
                try {
                    Date curTime = sdf.parse(curDateString);
                    Date birthDay = sdf.parse(curUser.getBirthday());
                    long diffInMilli = curTime.getTime() - birthDay.getTime();
                    long diffInDays = TimeUnit.DAYS.convert(diffInMilli, TimeUnit.MILLISECONDS);
                    if(diffInDays < 365){
                        age.setText("Puppy");
                    }
                    else{
                        age.setText(String.valueOf(diffInDays/365));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // handle make friend
                if (anotherUser){
                    if(curUser.isFriend(dataManager.getMyId())){ // case already friends
                        makeFriendButton.setVisibility(View.GONE);
                        unfriendButton.setVisibility(View.VISIBLE);
                    }
                    else if(curUser.isPendingRequest(dataManager.getMyId())){ // case already send friend request
                        makeFriendButton.setText("Pending request");
                        unfriendButton.setVisibility(View.GONE);
                    }
                    else
                    {
                        makeFriendButton.setText("Make Friend");
                        unfriendButton.setVisibility(View.GONE);
                    }

                    // make friend was pressed
                    makeFriendButton.setOnClickListener(v->{
                        if(curUser.isPendingRequest(dataManager.getMyId())){
                            curUser.removeFromPendingList(dataManager.getMyId());
                            dataManager.addToUsers(curUser);
                            makeFriendButton.setText("Make Friend");
                            dataManager.deleteNotification(NotificationTypes.FRIEND_REQUEST_RECEIVED_NOTIFICATION, curUser.getId(), null);
                        }
                        else{
                            curUser.addToPendingList(dataManager.getMyId());
                            dataManager.addToUsers(curUser);
                            makeFriendButton.setText("Pending request");
                            dataManager.sendNotification(NotificationTypes.FRIEND_REQUEST_RECEIVED_NOTIFICATION, curUser.getId(), null,null);
                            dataManager.db.collection("Users").document(dataManager.getMyId()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot != null)
                                            {
                                                User myUser = documentSnapshot.toObject(User.class);
                                                if (myUser != null && myUser.getDeviceToken() != null)
                                                {
                                                    dataManager.sendFirebaseNotification("You Have A New Friend Request!",
                                                            String.format("%s wants yo be your friend", dataManager.getUsernameFromSp()),
                                                            myUser.getDeviceToken());
                                                }
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(getContext(), "DB Error", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                    // unfriend was pressed
                    unfriendButton.setOnClickListener(v->{
                        curUser.removeFromFriendList(dataManager.getMyId());
                        dataManager.removeStringFromUserArrayField(dataManager.getMyId(),"friendList",curUser.getId());
                        dataManager.addToUsers(curUser);
                        removeFriendsFromPosts();
                        unfriendButton.setVisibility(View.GONE);
                        makeFriendButton.setVisibility(View.VISIBLE);
                        makeFriendButton.setText("Make Friend");

                    });
                }


                ////////////////////////// set profile picture //////////////////////////
                StorageReference profileImag = dataManager.storage.getReference().child(curUser.getProfilePhoto());
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
                        profileIm.setImageURI(Uri.fromFile(profileImFile));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // keeps the default profile image
                    }
                });

                ////////////////////////// friends Recycler //////////////////////////

                // query relevant friends:
                Query friendsQuery = dataManager.db.collection("Users")
                        .whereArrayContains("friendList",curUser.getId()); //todo: maybe limit

                FirestoreRecyclerOptions<User> friendsOptions = new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(friendsQuery,User.class).build();

                // create adapter
                friendsAdapter = new FirestoreRecyclerAdapter<User, myProfileFragment.RecyclerFriendsHolder>(friendsOptions) {

                    @NonNull
                    @NotNull
                    @Override
                    public myProfileFragment.RecyclerFriendsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.row_one_friend_in_profile_friends_list, parent, false);
                        return new myProfileFragment.RecyclerFriendsHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull RecyclerFriendsHolder holder, int position, @NonNull @NotNull User model) {
                        // set friends user name
                        holder.friendsName.setText(model.getUsername());

                        // set friends profile image
                        StorageReference profileImag = dataManager.storage.getReference().child(model.getProfilePhoto());
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
                                holder.friendProfileIm.setImageURI(Uri.fromFile(profileImFile));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // keeps the default profile image
                            }
                        });

                        // when a friend's image clicked- move to their profile
                        holder.friendProfileIm.setOnClickListener(v->{
                            Utils.moveBetweenFragments(R.id.the_screen, new myProfileFragment(model.getId()), getActivity(), "myProfile");
                        });
                    }
                };
                friendsRecycler.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
                friendsAdapter.startListening();
                friendsRecycler.setAdapter(friendsAdapter);


                ////////////////////////// Posts Recycler //////////////////////////

                // query relevant posts- only my posts:
                Query postsQuery = dataManager.db.collection("Posts")
                        .whereEqualTo("userId", curUser.getId())
                        .orderBy("timePosted", Query.Direction.DESCENDING); //todo: maybe limit

                FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(postsQuery,Post.class).build();

                // create adapter
                postsAdapter = new FirestoreRecyclerAdapter<Post, myProfileFragment.RecyclerPostsHolder>(options) {
                    @NonNull
                    @NotNull
                    @Override
                    public myProfileFragment.RecyclerPostsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.row_one_post_for_profile, parent, false);
                        return new myProfileFragment.RecyclerPostsHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull myProfileFragment.RecyclerPostsHolder holder, int position, @NonNull @NotNull Post model) {
                        // set the post image
                        StorageReference profileImag = dataManager.storage.getReference().child(model.getUploadedPhoto());
                        File localProfileImFile = null;
                        try {
                            localProfileImFile = File.createTempFile("post_photos", "g");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        File profileImFile = localProfileImFile;
                        profileImag.getFile(profileImFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                holder.postIm.setImageURI(Uri.fromFile(profileImFile));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // keeps the default profile image
                            }
                        });
                    }
                };
                postsRecycler.setLayoutManager(new GridLayoutManager(getContext(),3));
                postsAdapter.startListening();
                postsRecycler.setAdapter(postsAdapter);
            }
        });
    }

    protected void removeFriendsFromPosts(){
        // Access Posts in order to update the friends list
        Task<QuerySnapshot> result = dataManager.db.collection("Posts").get();
        result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (!documentSnapshots.isEmpty()) {
                    for(Post post: documentSnapshots.toObjects(Post.class)){
                        if(post.getUserId().equals(curUserId)){ // if it is his post, delete me from friend list
                            dataManager.removeStringFromPostArrayField(post.getPostId(),"friendList",dataManager.getMyId());
                        }
                        else if(post.getUserId().equals(dataManager.getMyId())){ // if it is my post, delete him from my friend list
                            dataManager.removeStringFromPostArrayField(post.getPostId(),"friendList",curUserId);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        this.postsAdapter.stopListening();
        this.friendsAdapter.stopListening();
    }

    // the view holder for the friends adapter
    private class RecyclerFriendsHolder extends RecyclerView.ViewHolder {
        private ImageView friendProfileIm;
        private TextView friendsName;

        public RecyclerFriendsHolder(View view) {
            super(view);
            this.friendProfileIm = view.findViewById(R.id.friend_profile_image_row_one_friend);
            this.friendsName = view.findViewById(R.id.friend_user_name_row_one_friend);

        }
    }

    // the view holder for the posts adapter
    private class RecyclerPostsHolder extends RecyclerView.ViewHolder {
        private ImageView postIm;

        public RecyclerPostsHolder(View view) {
            super(view);
            this.postIm = view.findViewById(R.id.post_image_one_post_profile_screen);
        }
    }
}