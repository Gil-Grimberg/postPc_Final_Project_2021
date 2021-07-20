package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class matchAFriendFragment extends Fragment {

    CentralBarkApp appInstance;
    TextView myName;
    TextView myDetailsDots;
    TextView aboutMe;
    ImageView dislike;
    ImageView like;
    String otherId; //todo: get the on screen user id
    User recommendedProfile;

    public matchAFriendFragment() {
        super(R.layout.fragment_match_a_friend);
        if(appInstance==null){
            appInstance = CentralBarkApp.getInstance();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // todo: find all views
        myName = view.findViewById(R.id.user_name_tinder);
        myDetailsDots = view.findViewById(R.id.details_tinder);
        aboutMe = view.findViewById(R.id.about_me_tinder);
        dislike = view.findViewById(R.id.x_view_tinder);
        like = view.findViewById(R.id.v_view_tinder);

        // todo: get recommended
        recommendedProfile = this.findRecommendedProfile();

        // todo: show on screen
        myName.setText(recommendedProfile.getUsername());
        String breed = recommendedProfile.getBreed();
        String city = recommendedProfile.getCity();
        String age = recommendedProfile.getBirthday();//todo: calculate age and convert to string!!

        //todo: setOnClick...
        like.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               User user = appInstance.getDataManager().getUserById(appInstance.getDataManager().getMyId());
               user.addToLikedList(otherId); //todo: add user to likedList
            }
        });

        dislike.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                User user = appInstance.getDataManager().getUserById(appInstance.getDataManager().getMyId());
                user.addToDislikedList(otherId); //todo: add user to dislikedList
            }
        });
    }

    private User findRecommendedProfile()
    {
        // todo: filter the best recommended matches for the user based on ML
        return new User();
    }



}