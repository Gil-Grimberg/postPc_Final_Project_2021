package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

public class openingFragment extends Fragment {

    Button signInButton;
    Button signUpButton;

    public openingFragment() {
        super(R.layout.fragment_opening);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signInButton = view.findViewById(R.id.sign_in_button_opening_screen);
        signUpButton = view.findViewById(R.id.sign_un_button_opening_screen);

        if(CentralBarkApp.getInstance().getDataManager().getMyId()!=null)
        {
            Utils.moveBetweenFragments(R.id.the_screen, new FeedFragment(), getActivity(), "feed");
            Utils.moveBetweenFragments(R.id.menu_bar, new menuFragment(), getActivity(), "menu");

        }

        signInButton.setOnClickListener(v ->
        {
            Utils.moveBetweenFragments(R.id.the_screen, new signInFragment(), getActivity(), "sign_in");
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        if (token != null)
                        signUpButton.setOnClickListener(v ->
                        {
                            Utils.moveBetweenFragments(R.id.the_screen, new signUpFragment(token), getActivity(), "sign_up");
                        });
                        else
                        {
                            Utils.moveBetweenFragments(R.id.the_screen, new signUpFragment(null), getActivity(), "sign_up");

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Utils.moveBetweenFragments(R.id.the_screen, new signUpFragment(null), getActivity(), "sign_up");
                    }
        });

        }
}