package com.example.centralbark_PostPc_2021;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.Period;

public class matchAFriendFragment extends Fragment {

    CentralBarkApp appInstance;
    TextView myName;
    TextView myDetailsDots;
    TextView aboutMe;
    ImageView profileImg;
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
        profileImg = view.findViewById(R.id.dog_image_tinder);
        findRecommendedProfile();

        like.setOnClickListener(v -> {
            Task<DocumentSnapshot> result_likeButt = appInstance.getDataManager().db.collection("Users").document(appInstance.getDataManager().getMyId()).get();
            result_likeButt.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null) {
                    myUser = documentSnapshot.toObject(User.class);
                    assert myUser != null;
                    if (!myUser.getLikedUsers().contains(otherId)) {
                        myUser.addToLikedList(otherId);
                    }
                    // update db
                    appInstance.getDataManager().addToUsers(myUser);
                    sendNotificationIfNecessary(otherId);

                    //find another profile and present
                    findRecommendedProfile();
                }
            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error: couldn't connect to database", Toast.LENGTH_LONG).show());


        });

        dislike.setOnClickListener(v -> {
            Task<DocumentSnapshot> result_dislikeButt = appInstance.getDataManager().db.collection("Users").document(appInstance.getDataManager().getMyId()).get();
            result_dislikeButt.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null) {
                    User myUser = documentSnapshot.toObject(User.class);
                    assert myUser != null;
                    if (!myUser.getDislikeUsers().contains(otherId)) {
                        myUser.addToDislikedList(otherId);
                    }
                    // update db
                    appInstance.getDataManager().addToUsers(myUser);

                    //find another profile and present
                    findRecommendedProfile();
                }
            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error: couldn't connect to database", Toast.LENGTH_LONG).show());
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void renderUI(User user) {
        // download and show profile image
          String photoAddress = user.getProfilePhoto();
          if (photoAddress==null)
          {
              Toast.makeText(getContext(), "Error: couldn't upload profile image", Toast.LENGTH_LONG).show();
              return;
          }
        if(!photoAddress.equals("default")){
            StorageReference profileImag = appInstance.getDataManager().storage.getReference().child(photoAddress);
            File localProfileImFile = null;
            try {
                localProfileImFile = File.createTempFile("profile_photos", "jpeg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            File finalLocalProfileImFile = localProfileImFile;
            profileImag.getFile(localProfileImFile).addOnSuccessListener(taskSnapshot ->
                    profileImg.setImageURI(Uri.fromFile(finalLocalProfileImFile))).addOnFailureListener(exception ->
                    profileImg.setImageResource(R.drawable.default_dog));
        }
        else{
            profileImg.setImageResource(R.drawable.default_dog);
        }

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
        String myDetails = age + " \u2022 " + city + " \u2022 " + breed;
        myDetailsDots.setText(myDetails);
        myName.setText(name);
        aboutMe.setText(about);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void findRecommendedProfile() {


        ArrayList<User> users = new ArrayList<>();
        Task<QuerySnapshot> result = appInstance.getDataManager().db.collection("Users").get();
        result.addOnSuccessListener(documentSnapshots -> {
            if (!documentSnapshots.isEmpty()) {
                users.addAll(documentSnapshots.toObjects(User.class));
                if (!users.isEmpty()) {
                    User me = new User();
                    boolean foundMe = false;
                    for (User user : users) {
                        if (user.getId().equals(appInstance.getDataManager().getMyId())) {
                            me = user;
                            foundMe = true;
                            break;
                        }
                    }
                    if (foundMe) {
                        for (User recommended : users) {
                            if (!(me.getLikedUsers().contains(recommended.getId()) || me.getDislikeUsers().contains(recommended.getId()) ||
                                    me.getId().equals(recommended.getId())||me.getFriendList().contains(recommended.getId()))) {
                                renderUI(recommended);
                                otherId = recommended.getId();
                                recommendedProfile = recommended;
                                return;
                            }
                        }
                        // if there are no new friends to offer, change to blank page with appropriate message
                        Utils.moveBetweenFragments(R.id.the_screen, new matchAFriendFragmentNoFriendsFragment(), getActivity(), "match_a_friend_no_friends");


                    }
                } else {
                    // if there are no new friends to offer, change to blank page with appropriate message
                    Utils.moveBetweenFragments(R.id.the_screen, new matchAFriendFragmentNoFriendsFragment(), getActivity(), "match_a_friend_no_friends");
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error: couldn't connect to database", Toast.LENGTH_LONG).show());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String calculateAge(String birthDay) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

        //convert String to LocalDate
        LocalDate birthDayDate = LocalDate.parse(birthDay, formatter);
        Period diff = Period.between(birthDayDate, LocalDate.now());
        return String.valueOf(diff.getYears());

    }

    private void sendNotificationIfNecessary(String matchUserId)
    {
        appInstance.getDataManager().db.collection("Users").document(matchUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null)
                    {
                        String myId = appInstance.getDataManager().getMyId();
                        User matchUser = documentSnapshot.toObject(User.class);
                        if (matchUser != null && matchUser.getLikedUsers().contains(myId))
                        {
                            appInstance.getDataManager().addStringToUserArrayField(appInstance.getDataManager().getMyId(), "friendList", matchUserId);
                            appInstance.getDataManager().addStringToUserArrayField(matchUserId, "friendList", appInstance.getDataManager().getMyId());
                            Utils.removeFriendsFromPosts(matchUserId, appInstance.getDataManager());
                            appInstance.getDataManager().sendNotification(NotificationTypes.TINDER_MATCH_NOTIFICATION, matchUserId, null, null);
                            appInstance.getDataManager().sendMatchNotificationToMyself(matchUserId, matchUser.getProfilePhoto(), matchUser.getUsername());
                            appInstance.getDataManager().sendFirebaseNotification("It's a Match!",
                                    String.format("you have a match with %s!", appInstance.getDataManager().getUsernameFromSp()),
                                    matchUser.getDeviceToken());
                            appInstance.getDataManager().db.collection("Users").document(myId).get()
                                    .addOnSuccessListener(documentSnapshot1 -> {
                                        if (documentSnapshot1 != null)
                                        {
                                            User myUser = documentSnapshot1.toObject(User.class);
                                            if (myUser != null && myUser.getDeviceToken() != null)
                                            {
                                                appInstance.getDataManager().sendFirebaseNotification("It's a Match!",
                                                        String.format("you have a match with %s!", matchUser.getUsername()),
                                                        myUser.getDeviceToken());
                                            }
                                        }
                                    }).addOnFailureListener(e -> Toast.makeText(getContext(), "DB Error", Toast.LENGTH_SHORT).show());
                        }
                    }
                }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "DB error. Couldn't send notification", Toast.LENGTH_LONG).show());
    }
}