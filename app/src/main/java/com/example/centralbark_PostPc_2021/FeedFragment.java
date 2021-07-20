package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FeedFragment extends Fragment {
    private CentralBarkApp appInstance;
    private ImageView notification;
    private RecyclerView recyclerViewPosts;

    public FeedFragment() {
        super(R.layout.fragment_feed);
        if(appInstance==null){
            this.appInstance = CentralBarkApp.getInstance();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);
        notification = view.findViewById(R.id.notification_button_feed_screen);
        recyclerViewPosts = view.findViewById(R.id.post_recyclerview_feed_screen);


    }

    public int findAmountOfPosts(){

    }
}