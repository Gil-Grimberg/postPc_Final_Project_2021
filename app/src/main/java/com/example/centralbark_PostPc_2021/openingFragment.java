package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;

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

        signInButton.setOnClickListener(v ->
        {
            Utils.moveBetweenFragments(R.id.the_screen, new signInFragment(), getActivity(), "sign_in");
        });

        signUpButton.setOnClickListener(v ->
        {
            Utils.moveBetweenFragments(R.id.the_screen, new signUpFragment(), getActivity(), "sign_up");
        });
        }
}