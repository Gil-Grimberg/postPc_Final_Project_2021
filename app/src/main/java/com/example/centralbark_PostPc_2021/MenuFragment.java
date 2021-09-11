package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class MenuFragment extends Fragment {

    ImageView searchButton;
    ImageView tinderButton;
    ImageView homeButton;
    ImageView locationButton;
    ImageView profileButton;
    String myUserId;

    public MenuFragment() {
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

        searchButton.setOnClickListener(v->{
            ArrayList<ImageView> makeSmaller = addToMakeSmallerLst(tinderButton, homeButton, locationButton, profileButton);
            sizeUpIcon(searchButton, makeSmaller);
            Utils.moveBetweenFragments(R.id.the_screen, new searchAccountFragment(this), getActivity(), "search_accounts");
        });
        tinderButton.setOnClickListener(v->{
            ArrayList<ImageView> makeSmaller =addToMakeSmallerLst(searchButton, homeButton, locationButton, profileButton);
            sizeUpIcon(tinderButton, makeSmaller);
            Utils.moveBetweenFragments(R.id.the_screen, new matchAFriendFragment(), getActivity(), "match_a_friend");
        });
        homeButton.setOnClickListener(v->{
            ArrayList<ImageView> makeSmaller =addToMakeSmallerLst(tinderButton, searchButton, locationButton, profileButton);
            sizeUpIcon(homeButton, makeSmaller);
            Utils.moveBetweenFragments(R.id.the_screen, new FeedFragment(), getActivity(), "feed");
        });
        locationButton.setOnClickListener(v->{
            ArrayList<ImageView> makeSmaller = addToMakeSmallerLst(tinderButton, homeButton, searchButton, profileButton);
            sizeUpIcon(locationButton, makeSmaller);
            Utils.moveBetweenFragments(R.id.the_screen, new MapsFragment(), getActivity(), "maps");
        });
        profileButton.setOnClickListener(v->{
            ArrayList<ImageView> makeSmaller =addToMakeSmallerLst(tinderButton, homeButton, locationButton, searchButton);
            sizeUpIcon(profileButton, makeSmaller);
            myUserId = CentralBarkApp.getInstance().getDataManager().getMyId();
            Utils.moveBetweenFragments(R.id.the_screen, new myProfileFragment(myUserId), getActivity(), "myProfile");
        });
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // Do something with the result
                ArrayList<ImageView> makeSmaller =addToMakeSmallerLst(tinderButton, homeButton, profileButton, searchButton);
                sizeUpIcon(locationButton, makeSmaller);
            }
        });
    }
    private ArrayList<ImageView> addToMakeSmallerLst(ImageView makeSmaller1, ImageView makeSmaller2, ImageView makeSmaller3, ImageView makeSmaller4){
        ArrayList<ImageView> makeSmaller = new ArrayList<>();
        makeSmaller.add(makeSmaller1);
        makeSmaller.add(makeSmaller2);
        makeSmaller.add(makeSmaller3);
        makeSmaller.add(makeSmaller4);
        return makeSmaller;
    }

    private void sizeUpIcon(ImageView makeBigger, ArrayList<ImageView> makeSmaller){
        final float scale = getResources().getDisplayMetrics().density;
        int fiftyInDp  = (int) (50 * scale);
        makeBigger.getLayoutParams().height = fiftyInDp;
        makeBigger.getLayoutParams().width = fiftyInDp;
        makeBigger.requestLayout();

        for(ImageView imageView:makeSmaller){
            int thirtyInDp  = (int) (30 * scale);
            imageView.getLayoutParams().height = thirtyInDp;
            imageView.getLayoutParams().width = thirtyInDp;
            imageView.requestLayout();
        }
    }

}