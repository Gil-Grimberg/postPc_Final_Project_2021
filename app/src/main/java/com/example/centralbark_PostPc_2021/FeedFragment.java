package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {
    private DataManager dataManager;
    private ImageView notification;
    private RecyclerView recyclerViewPosts;
    final private int maxPostsInFeed = 10;

    public FeedFragment() {
        super(R.layout.fragment_feed);
        if(dataManager ==null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);
        notification = view.findViewById(R.id.notification_button_feed_screen);
        recyclerViewPosts = view.findViewById(R.id.post_recyclerview_feed_screen);


    }

    public List<Post> findAllRelevantPosts(){
        // todo: dont use this- copy every time that want the posts
        List<Post> allRelevantPosts = new ArrayList<>();
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

        if(allRelevantPosts.size()>this.maxPostsInFeed){
            return allRelevantPosts.subList(0,this.maxPostsInFeed);
        }
        return allRelevantPosts;
    }

}