package com.example.centralbark_PostPc_2021;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class matchAFriendFragmentNoFriendsFragment extends Fragment {

    CentralBarkApp appInstance;
    TextView myName;
    TextView myDetailsDots;
    TextView aboutMe;
    ImageView dislike;
    ImageView like;
    String otherId;
    User recommendedProfile;
    User myUser;

    public matchAFriendFragmentNoFriendsFragment() {
        super(R.layout.fragment_match_a_friend_no_friends);
        if (appInstance == null) {
            appInstance = CentralBarkApp.getInstance();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String myId = appInstance.getDataManager().getMyId();

        findRecommendedProfile();


    }

    private void findRecommendedProfile() {
        // todo: filter the best recommended matches for the user based on ML

        ArrayList<User> users = new ArrayList<>();
        Task<QuerySnapshot> result = appInstance.getDataManager().db.collection("Users").get();
        result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (!documentSnapshots.isEmpty()) {
//                    User myUser;
                    users.addAll(documentSnapshots.toObjects(User.class));
                    if (!users.isEmpty()) {
//                        Random generator = new Random();
//                        int randomIndex = generator.nextInt(users.size());
//                        // find another profile and present
//                        User recommended = users.get(randomIndex);
                        User me = new User();
                        boolean foundMe = false;
                        for (User user:users)
                        {
                            if (user.getId().equals(appInstance.getDataManager().getMyId()))
                            {
                                me = user;
                                foundMe = true;
                                break;
                            }
                        }
                        if (foundMe) {
                            for (User recommended : users) {
                                if (me.getLikedUsers().contains(recommended.getId()) || me.getDislikeUsers().contains(recommended.getId()) || me.getId().equals(recommended.getId())) {

                                } else {
                                    // switch back to matchAFriendFragment
                                    Utils.moveBetweenFragments(R.id.the_screen, new matchAFriendFragment(), getActivity(), "match_a_friend");
                                }
                            }
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

}