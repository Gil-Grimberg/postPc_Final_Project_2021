package com.example.centralbark_PostPc_2021;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
            Utils.moveBetweenFragments(R.id.menu_bar, new MenuFragment(), getActivity(), "menu");

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
                    public void onFailure(@NotNull Exception e) {
                        Utils.moveBetweenFragments(R.id.the_screen, new signUpFragment(null), getActivity(), "sign_up");
                    }
        });

        // handle back pressed
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

    }
}