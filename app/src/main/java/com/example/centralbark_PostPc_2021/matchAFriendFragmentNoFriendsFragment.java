package com.example.centralbark_PostPc_2021;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class matchAFriendFragmentNoFriendsFragment extends Fragment {

    CentralBarkApp appInstance;

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
        findRecommendedProfile();
    }

    private void findRecommendedProfile() {

        ArrayList<User> users = new ArrayList<>();
        Task<QuerySnapshot> result = appInstance.getDataManager().db.collection("Users").get();
        result.addOnSuccessListener(documentSnapshots -> {
            if (!documentSnapshots.isEmpty()) {
                users.addAll(documentSnapshots.toObjects(User.class));
                if (!users.isEmpty()) {
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
                            if (!(me.getLikedUsers().contains(
                                    recommended.getId()) || me.getDislikeUsers().contains(recommended.getId()) || me.getId().equals(recommended.getId()) || me.getFriendList().contains(recommended.getId()))) {

                                // switch back to matchAFriendFragment
                                Utils.moveBetweenFragments(R.id.the_screen, new matchAFriendFragment(), getActivity(), "match_a_friend");
                            }
                        }
                    }
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error: couldn't connect to database", Toast.LENGTH_LONG).show());
    }
}