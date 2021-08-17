package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.ImageButton;


public class menuFragment extends Fragment {

    ImageButton searchButton;
    ImageButton tinderButton;
    ImageButton homeButton;
    ImageButton locationButton;
    ImageButton profileButton;

    public menuFragment() {
        super(R.layout.fragment_menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchButton = view.findViewById(R.id.searchImage_ImageView);
        tinderButton = view.findViewById(R.id.tinderButton_ImageButton_fragmentMatchAFriend);
        homeButton = view.findViewById(R.id.homeButton_ImageButton_fragmentFeed);
        locationButton = view.findViewById(R.id.locationButton_ImageButton_fragmentSearchPlaces);
        profileButton = view.findViewById(R.id.profileButton_ImageButton_fragmentMyProfile);

        searchButton.setOnClickListener(v->
                Utils.moveBetweenFragments(R.id.the_screen, new searchAccountFragment(), getActivity(), "search_accounts"));
        tinderButton.setOnClickListener(v->
                Utils.moveBetweenFragments(R.id.the_screen, new matchAFriendFragment(), getActivity(), "match_a_friend"));
        homeButton.setOnClickListener(v->
                Utils.moveBetweenFragments(R.id.the_screen, new FeedFragment(), getActivity(), "feed"));
        locationButton.setOnClickListener(v->
                Utils.moveBetweenFragments(R.id.the_screen, new MapsFragment(), getActivity(), "maps"));
        profileButton.setOnClickListener(v->
                Utils.moveBetweenFragments(R.id.the_screen, new myProfileFragment(), getActivity(), "myProfile"));
    }

}