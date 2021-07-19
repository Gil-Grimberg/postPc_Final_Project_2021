package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class openingFragment extends Fragment {

    Button signInButton;
    Button signUpButton;

    public openingFragment() {
        super(R.layout.fragment_opening);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signInButton = view.findViewById(R.id.sign_in_button_opening_screen);
        signUpButton = view.findViewById(R.id.sign_un_button_opening_screen);

        //todo: fix the bellow code

        signInButton.setOnClickListener(v ->
        {
            Fragment signInFragment = new signInFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.signInFragment, signInFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        signUpButton.setOnClickListener(v ->
        {
            Fragment signUpFragment = new signInFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.signUpFragment, signUpFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


    }
}