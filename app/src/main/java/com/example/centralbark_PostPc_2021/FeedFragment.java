package com.example.centralbark_PostPc_2021;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class FeedFragment extends Fragment {
    private DataManager dataManager;
    private ImageView notificationButton;
    private ImageView addPostButton;
    private RecyclerView recyclerViewPosts; // todo: recycler on menu bar
    private FirestoreRecyclerAdapter postsAdapter;

    ActivityResultContracts.RequestMultiplePermissions requestMultiplePermissionsContract;
    ActivityResultLauncher<String[]> multiplePermissionActivityResultLauncher;

    public FeedFragment() {
        super(R.layout.fragment_feed);
        if(dataManager ==null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

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
        super.onViewCreated(view, savedInstanceState);
        this.notificationButton = view.findViewById(R.id.notification_button_feed_screen);
        this.recyclerViewPosts = view.findViewById(R.id.post_recyclerview_feed_screen);
        this.addPostButton = view.findViewById(R.id.add_post_button_feed_screen);


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

            @Override
            protected void onBindViewHolder(@NonNull @NotNull RecyclerPostsHolder holder, int position, @NonNull @NotNull Post model) {
                // set the user name
                holder.userNameTitle.setText(model.getUserName());
                holder.userNameContent.setText(model.getUserName());

                // set the content of the post
                holder.postContent.setText(model.getContent());

                // set the number of likes, and update
                holder.numOfLikes.setText(String.valueOf(model.getNumOfLikes()));
                holder.likeButton.setOnClickListener(v -> {
                    if(model.isUserLikesPost(dataManager.getMyId())){
                        model.removeLike(dataManager.getMyId());
                    }
                    else{
                        model.addLike(dataManager.getMyId());
                    }
                    holder.numOfLikes.setText(String.valueOf(model.getNumOfLikes()));
                });

                // set the time of the post
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String curDateString = sdf.format(new Date());
                try {
                    Date curTime = sdf.parse (curDateString);
                    Date postTime = model.parseStringToDate();
                    long diffInMilli = curTime.getTime() - postTime.getTime();
                    long diffInHours = TimeUnit.HOURS.convert(diffInMilli, TimeUnit.MILLISECONDS);
                    if (diffInHours >= 1){
                        holder.postTime.setText(diffInHours + " hours ago");
                    }
                    else{
                        long diffInMin = TimeUnit.MINUTES.convert(diffInMilli, TimeUnit.MILLISECONDS);
                        holder.postTime.setText(diffInMin + " minutes ago");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // set users profile image
                StorageReference profileImag = dataManager.storage.getReference().child(model.getUserProfilePhoto());
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
                        holder.profileIm.setImageURI(Uri.fromFile(profileImFile));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // keeps the default profile image
                    }
                });

                // set users post image
                StorageReference postImag = dataManager.storage.getReference().child(model.getUploadedPhoto());
                File localPostImFile = null;
                try {
                    localPostImFile = File.createTempFile("post_photos", "g");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File finalLocalPostImFile = localPostImFile;
                postImag.getFile(localPostImFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        holder.postIm.setImageURI(Uri.fromFile(finalLocalPostImFile));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // todo: something todo?
                    }
                });
            }
        };

        this.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this.getContext(),RecyclerView.VERTICAL,false));
        this.recyclerViewPosts.setAdapter(postsAdapter);

        // move to add post screen
        this.addPostButton.setOnClickListener(v -> {
            Utils.moveBetweenFragments(R.id.the_screen, new AddPostFragment(), getActivity(), "add_post");
        });
    }

//    public List<Post> findAllRelevantPosts(){
//        // todo: dont use this- copy every time that want the posts
//        List<Post> allRelevantPosts = new ArrayList<>();
//        String myId = this.dataManager.getMyId();
//        this.dataManager.db.collection("Users").document(myId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
////            @Override
////            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                User myUser = documentSnapshot.toObject(User.class);
//                ArrayList<String> allRelevantIds = new ArrayList<>();
//                allRelevantIds.add(myId);
//                allRelevantIds.addAll(myUser.getFriendList());
//
//                Task<QuerySnapshot> result = dataManager.db.collection("Posts").get();
//                result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<DocumentSnapshot> allPosts = queryDocumentSnapshots.getDocuments();
//                        for (DocumentSnapshot post : allPosts) {
//                            Post curPost = post.toObject(Post.class);
//                            if (allRelevantIds.contains(curPost.getUserId())) {
//                                allRelevantPosts.add(curPost);
//                            }
//                        }
//                    }
//                });
//            }
//        });
//        Collections.sort(allRelevantPosts, new SortPosts());
//
//        if(allRelevantPosts.size()>this.maxPostsInFeed){
//            return allRelevantPosts.subList(0,this.maxPostsInFeed);
//        }
//        return allRelevantPosts;
//    }

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
        }
    }
}

