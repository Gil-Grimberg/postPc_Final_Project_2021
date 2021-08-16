package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class settingsFragment extends Fragment {
    private DataManager dataManager;
    private ImageView goToPrivacySettings;
    private ImageView goToAccountSettings;
    private TextView logOut;

    public settingsFragment() {
        super(R.layout.fragment_settings);
        if(dataManager ==null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);

        this.goToPrivacySettings = view.findViewById(R.id.enter_to_privacy_button_setting_screen);
        this.goToAccountSettings = view.findViewById(R.id.enter_to_account_button_setting_screen);
        this.logOut = view.findViewById(R.id.log_out_button_setting_screen);

        // move to privacy setting fragment
        this.goToPrivacySettings.setOnClickListener(v -> {
            Utils.moveBetweenFragments(R.id.the_screen, new privacySettingsFragment(), getActivity(), "privacySettings");
        });

        // move to account setting fragment
        this.goToAccountSettings.setOnClickListener(v -> {
            Utils.moveBetweenFragments(R.id.the_screen, new accountSettingFragment(), getActivity(), "accountSettings");
        });

        // log out button
        this.logOut.setOnClickListener(v->{
            Utils.moveBetweenFragments(R.id.the_screen, new openingFragment(), getActivity(), "opening");
        });
    }


}