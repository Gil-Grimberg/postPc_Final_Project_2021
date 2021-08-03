package com.example.centralbark_PostPc_2021;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Period;

public class matchAFriendFragment extends Fragment {

    CentralBarkApp appInstance;
    TextView myName;
    TextView myDetailsDots;
    TextView aboutMe;
    ImageView dislike;
    ImageView like;
    String otherId;
    User recommendedProfile;
    User myUser;

    public matchAFriendFragment() {
        super(R.layout.fragment_match_a_friend);
        if (appInstance == null) {
            appInstance = CentralBarkApp.getInstance();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // find all views
        myName = view.findViewById(R.id.user_name_tinder);
        myDetailsDots = view.findViewById(R.id.details_tinder);
        aboutMe = view.findViewById(R.id.about_me_tinder);
        dislike = view.findViewById(R.id.x_view_tinder);
        like = view.findViewById(R.id.v_view_tinder);

        String myId = appInstance.getDataManager().getMyId();

//        ArrayList<User> users = new ArrayList<>();
//        Task<QuerySnapshot> result = appInstance.getDataManager().db.collection("Users").get();
//        result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot documentSnapshots) {
//                if (!documentSnapshots.isEmpty()) {
//                    users.addAll(documentSnapshots.toObjects(User.class));
//                    for (User user : users) {
//                        if (user.getId().equals(myId)) {
//                            myUser = user;
//
//                        }
//                    }
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                Toast.makeText(getContext(), "Error: couldn't connect to database", Toast.LENGTH_LONG).show();
//            }
//        });

        findRecommendedProfile();
        // show on screen
//        if (recommendedProfile != null) {
//            renderUI(recommendedProfile);
//        } else {
//            Toast.makeText(getContext(), "There isn't any new friend to present", Toast.LENGTH_LONG).show();
//
//        }


        // todo: downLoad dog image and show on screen


        like.setOnClickListener(v -> {

            ArrayList<User> users_likeButt = new ArrayList<>();
            Task<QuerySnapshot> result_likeButt = appInstance.getDataManager().db.collection("Users").get();
            result_likeButt.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
//                        users[0] = documentSnapshot.toObject(User.class);
                    if (!documentSnapshots.isEmpty()) {
                        users_likeButt.addAll(documentSnapshots.toObjects(User.class));
                        for (User user : users_likeButt) {
                            if (user.getId().equals(myId)) {
                                myUser = user;
                                if (!myUser.getLikedUsers().contains(otherId)) {
                                    myUser.addToLikedList(otherId);
                                }
                                // update db
                                appInstance.getDataManager().addToUsers(myUser); // todo: make sure it updates the existing user!
                                //find another profile and present
                                findRecommendedProfile();
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


        });

        dislike.setOnClickListener(v -> {
//            String myId = appInstance.getDataManager().getMyId();

            ArrayList<User> users_dislikeButt = new ArrayList<>();
            Task<QuerySnapshot> result_dislikeButt = appInstance.getDataManager().db.collection("Users").get();
            result_dislikeButt.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
//                        users[0] = documentSnapshot.toObject(User.class);
                    if (!documentSnapshots.isEmpty()) {
                        User myUser;
                        users_dislikeButt.addAll(documentSnapshots.toObjects(User.class));
                        for (User user : users_dislikeButt) {
                            if (user.getId().equals(myId)) {
                                myUser = user;
                                if (!myUser.getDislikeUsers().contains(otherId)) {
                                    myUser.addToDislikedList(otherId);
                                }
                                // update db
                                appInstance.getDataManager().addToUsers(myUser); // todo: make sure it updates the existing user!
                                //find another profile and present
                                findRecommendedProfile();
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
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void renderUI(User user) {
        myName.setText(user.getUsername());
        String breed = user.getBreed();
        String city = user.getCity();
        String age = "nan";
        try {
            age = calculateAge(user.getBirthday()); // calculate age and convert to string
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Error: couldn't calculate age!", Toast.LENGTH_LONG).show();
        }

        String name = user.getUsername();
        String about = user.getSelfSummary();
        String myDetails = age + "\u2022" + city + "\u2022" + breed;
        myDetailsDots.setText(myDetails);
        myName.setText(name);
        aboutMe.setText(about);
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
                                    renderUI(recommended);
                                    otherId = recommended.getId();
                                    recommendedProfile = recommended;
                                    return;
                                }
                            }
                            Toast.makeText(getContext(), "passed over all users, there isn't any new friend to present", Toast.LENGTH_LONG).show();
                            // todo: if there are no new friends to offer, change to blank page with appropriate message

                        }
                    } else {
                        Toast.makeText(getContext(), "There are no users at all! there isn't any new friend to present", Toast.LENGTH_LONG).show();
                        // todo: if there are no new friends to offer, change to blank page with appropriate message

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String calculateAge(String birthDay) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

        //convert String to LocalDate
        LocalDate birthDayDate = LocalDate.parse(birthDay, formatter);
        Period diff = Period.between(birthDayDate, LocalDate.now());
        return String.valueOf(diff.getYears());

    }


}