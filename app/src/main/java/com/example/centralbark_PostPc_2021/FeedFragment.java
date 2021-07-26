package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;

public class FeedFragment extends Fragment {
    private DataManager dataManager;
    private ImageView notification;
    private RecyclerView recyclerViewPosts;

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

//    public int findAmountOfPosts(){
//        User myUser = this.dataManager.getUserById(this.dataManager.getMyId());
//        ArrayList<Post> allRelevantPosts = new ArrayList<>();
//        allRelevantPosts.addAll(this.dataManager.getPostsById(myUser.getId())); // add all my posts
//        for(String friendId: myUser.getFriendList()){
//            allRelevantPosts.addAll(this.dataManager.getPostsById(friendId)); // add all my friends posts
//        }
//        // todo: sort allRelevantPosts by time (comparator in Post) and show only the X relevant
//    }
}