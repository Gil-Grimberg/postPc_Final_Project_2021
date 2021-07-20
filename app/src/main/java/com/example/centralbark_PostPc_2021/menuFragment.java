package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchButton = view.findViewById(R.id.searchButton_ImageButton_fragmentSearchAccount);
        tinderButton = view.findViewById(R.id.tinderButton_ImageButton_fragmentMatchAFriend);
        homeButton = view.findViewById(R.id.homeButton_ImageButton_fragmentFeed);
        locationButton = view.findViewById(R.id.locationButton_ImageButton_fragmentSearchPlaces);
        profileButton = view.findViewById(R.id.profileButton_ImageButton_fragmentMyProfile);

        searchButton.setOnClickListener(v->
                Utils.moveBetweenFragments(R.id.the_screen, new searchAcountFragment(), getActivity()));
        tinderButton.setOnClickListener(v->
                Utils.moveBetweenFragments(R.id.the_screen, new matchAFriendFragment(), getActivity()));
        homeButton.setOnClickListener(v->
                Utils.moveBetweenFragments(R.id.the_screen, new FeedFragment(), getActivity()));
        locationButton.setOnClickListener(v->
                Utils.moveBetweenFragments(R.id.the_screen, new MapsFragment(), getActivity()));
        profileButton.setOnClickListener(v->
                Utils.moveBetweenFragments(R.id.the_screen, new myProfileFragment(), getActivity()));
    }
}