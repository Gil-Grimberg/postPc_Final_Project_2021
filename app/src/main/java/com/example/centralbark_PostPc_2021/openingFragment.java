package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

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

        signUpFragment signUpFragment = (signUpFragment) getActivity().getSupportFragmentManager().findFragmentByTag("sign_up");
        signInFragment signInFragment = (signInFragment) getActivity().getSupportFragmentManager().findFragmentByTag("sign_in");
        if (signUpFragment != null)
        {
            Utils.moveBetweenFragments(R.id.the_screen, signUpFragment, getActivity(), "sign_up");
        }

        else if (signInFragment != null)
        {
            Utils.moveBetweenFragments(R.id.the_screen, signInFragment, getActivity(), "sign_in");
        }

        else
        {
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
}