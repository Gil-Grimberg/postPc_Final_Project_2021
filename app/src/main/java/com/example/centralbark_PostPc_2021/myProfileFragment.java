package com.example.centralbark_PostPc_2021;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
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

    public myProfileFragment() {
        super(R.layout.fragment_my_profile);
        if(dataManager ==null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
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

        this.dataManager.db.collection("Users").document(this.dataManager.getMyId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User myUser = documentSnapshot.toObject(User.class);
                // set user name
                userName.setText(myUser.getUsername());

                // set city
                city.setText(myUser.getCity());

                //set Age if less than 1 write "Puppy"
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String curDateString = sdf.format(new Date());
                try {
                    Date curTime = sdf.parse(curDateString);
                    Date birthDay = sdf.parse(myUser.getBirthday());
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

                // set profile picture
                StorageReference profileImag = dataManager.storage.getReference().child(myUser.getProfilePhoto());
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
                        .whereArrayContains("friendList",myUser.getId()); //todo: maybe limit

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
                    }
                };
                friendsRecycler.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
                friendsAdapter.startListening();
                friendsRecycler.setAdapter(friendsAdapter);


                ////////////////////////// Posts Recycler //////////////////////////

                // query relevant posts- only my posts:
                Query postsQuery = dataManager.db.collection("Posts").whereEqualTo("userId", myUser.getId()); //todo: maybe limit

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