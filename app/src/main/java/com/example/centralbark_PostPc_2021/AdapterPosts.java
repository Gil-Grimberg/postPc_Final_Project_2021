package com.example.centralbark_PostPc_2021;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.RecyclerPosts>{

    static class RecyclerPosts extends RecyclerView.ViewHolder{
        private ImageView profileIm;
        private TextView userName;
        private ImageView postIm;
        private Button likeButton;
        private TextView numOfLikes;
        private TextView postTime;
        public RecyclerPosts(View view){
            super(view);
            this.profileIm = view.findViewById(R.id.profile_image_one_post_screen);
            this.userName = view.findViewById(R.id.user_name_textview_one_post_screen);
            this.postIm = view.findViewById(R.id.post_image_one_post_screen);
            this.likeButton = view.findViewById(R.id.like_button_one_post_screen);
            this.numOfLikes = view.findViewById(R.id.num_of_likes_textview_one_post_screen);
            this.postTime = view.findViewById(R.id.post_time_textview_one_post_screen);
        }
    }

    private DataManager dataManager;
    private Context context;
    private FeedFragment feedFragment;
    final private int maxPostsInFeed = 10;

    public AdapterPosts(Context context, FeedFragment feedFragment){
        this.context = context;
        this.dataManager = CentralBarkApp.getInstance().getDataManager();
        this.feedFragment = feedFragment;
    }
    @NotNull
    @Override
    public RecyclerPosts onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_one_post, parent, false);
        return new RecyclerPosts(view);
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerPosts holder, int position) {

        // get all posts that are relevant to me from database:
        ArrayList<Post> allRelevantPosts = new ArrayList<>();
        String myId = this.dataManager.getMyId();
        this.dataManager.db.collection("Users").document(myId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User myUser = documentSnapshot.toObject(User.class);
                ArrayList<String> allRelevantIds = new ArrayList<>();
                allRelevantIds.add(myId);
                allRelevantIds.addAll(myUser.getFriendList());

                Task<QuerySnapshot> result = dataManager.db.collection("Posts").get();
                result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> allPosts = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot post : allPosts) {
                            Post curPost = post.toObject(Post.class);
                            if (allRelevantIds.contains(curPost.getUserId())) {
                                allRelevantPosts.add(curPost);
                            }
                        }
                    }
                });
            }
        });
        Collections.sort(allRelevantPosts, new SortPosts());

        List<Post> allRelevantPostsFinal = new ArrayList<>();
        if(allRelevantPosts.size()>this.maxPostsInFeed){
            allRelevantPostsFinal = allRelevantPosts.subList(0,this.maxPostsInFeed);
        }
        else{
            allRelevantPostsFinal = allRelevantPosts;
        }

        // extract the current post
        Post curPost = allRelevantPostsFinal.get(position);

        // set users profile image
        String profilrImName = "profile_photos/" + curPost.getUserId() + "jpg";
        StorageReference profileImag = this.dataManager.storage.getReference().child(profilrImName);
        File localProfileImFile = null;
        try {
            localProfileImFile = File.createTempFile("profile_photos/", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File profileImFile = localProfileImFile;
        profileImag.getFile(localProfileImFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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

        // set users profile image
        String postImName = "post_photos/" + curPost.getUserId() + "jpg";
        StorageReference postImag = this.dataManager.storage.getReference().child(postImName);
        File localPostImFile = null;
        try {
            localPostImFile = File.createTempFile("post_photos/", "jpg");
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

    @Override
    public int getItemCount() {
        // get all posts that are relevant to me from database:
        ArrayList<Post> allRelevantPosts = new ArrayList<>();
        String myId = this.dataManager.getMyId();
        this.dataManager.db.collection("Users").document(myId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User myUser = documentSnapshot.toObject(User.class);
                ArrayList<String> allRelevantIds = new ArrayList<>();
                allRelevantIds.add(myId);
                allRelevantIds.addAll(myUser.getFriendList());

                Task<QuerySnapshot> result = dataManager.db.collection("Posts").get();
                result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> allPosts = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot post : allPosts) {
                            Post curPost = post.toObject(Post.class);
                            if (allRelevantIds.contains(curPost.getUserId())) {
                                allRelevantPosts.add(curPost);
                            }
                        }
                    }
                });
            }
        });

        // return the size or the max size for amount of posts
        return Math.min(allRelevantPosts.size(), this.maxPostsInFeed);
    }

}
