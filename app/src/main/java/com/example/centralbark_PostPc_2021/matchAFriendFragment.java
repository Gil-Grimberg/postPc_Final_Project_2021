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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        if (appInstance == null) {
            appInstance = CentralBarkApp.getInstance();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // todo: find all views
        myName = view.findViewById(R.id.user_name_tinder);
        myDetailsDots = view.findViewById(R.id.details_tinder);
        aboutMe = view.findViewById(R.id.about_me_tinder);
        dislike = view.findViewById(R.id.x_view_tinder);
        like = view.findViewById(R.id.v_view_tinder);

        // todo: get recommended
//        recommendedProfile = this.findRecommendedProfile();
        recommendedProfile = new User("Kevin", "1234", "sfsf@sdf.com", "sdfsf", "14/02/1993", "good breed", "tel aviv", true, "i like to run!");
        otherId = recommendedProfile.getId();
        // todo: show on screen
        myName.setText(recommendedProfile.getUsername());
        String breed = recommendedProfile.getBreed();
        String city = recommendedProfile.getCity();
        String age = recommendedProfile.getBirthday();//todo: calculate age and convert to string!!
        String name = recommendedProfile.getUsername();
        String about = recommendedProfile.getSelfSummary();
        String myDetails = "3" + "\u2022" + city + "\u2022" + breed;
        myDetailsDots.setText(myDetails);
        myName.setText(name);
        aboutMe.setText(about);

        // todo: downLoad dog image and show on screen

        //todo: setOnClick...
        like.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myId = appInstance.getDataManager().getMyId();

                ArrayList<User> users = new ArrayList<>();
                Task<QuerySnapshot> result = appInstance.getDataManager().db.collection("Users").get();
                result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
//                        users[0] = documentSnapshot.toObject(User.class);
                        if (!documentSnapshots.isEmpty())
                        {
                            User myUser;
                            users.addAll(documentSnapshots.toObjects(User.class));
                            for (User user: users)
                            {
                                if (user.getId().equals(myId))
                                {
                                    myUser = user;
                                    myUser.addToLikedList(otherId);
                                    // todo: update db
                                    // todo: find another profile and present
                                    break;
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Error: couldn't connect to database", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = appInstance.getDataManager().getUserById(appInstance.getDataManager().getMyId());
                user.addToDislikedList(otherId);
                // todo: update db
                // todo: find another profile and present
            }
        });
    }

    private User findRecommendedProfile() {
        // todo: filter the best recommended matches for the user based on ML
        return new User();
    }


}