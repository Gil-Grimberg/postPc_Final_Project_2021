package com.boss.centralbark_PostPc_2021;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class settingsFragment extends Fragment {
    private DataManager dataManager;
    private ImageView goToPasswordSettings;
    private ImageView goToAccountSettings;
    private ImageView goToAboutSettings;
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

        this.goToPasswordSettings = view.findViewById(R.id.enter_to_password_button_setting_screen);
        this.goToAccountSettings = view.findViewById(R.id.enter_to_account_button_setting_screen);
        this.goToAboutSettings = view.findViewById(R.id.enter_to_about_button_setting_screen);
        this.logOut = view.findViewById(R.id.log_out_button_setting_screen);

        // move to password setting fragment
        this.goToPasswordSettings.setOnClickListener(v -> {
            Utils.moveBetweenFragments(R.id.the_screen, new passwordSettingsFragment(), getActivity(), "passwordSettings");
        });

        // move to account setting fragment
        this.goToAccountSettings.setOnClickListener(v -> {
            Utils.moveBetweenFragments(R.id.the_screen, new accountSettingFragment(), getActivity(), "accountSettings");
        });

        // move to about setting fragment
        this.goToAboutSettings.setOnClickListener(v -> {
            Utils.moveBetweenFragments(R.id.the_screen, new aboutSettingFragment(), getActivity(), "aboutSettings");
        });

        // log out button
        this.logOut.setOnClickListener(v->{
            dataManager.removeDeviceTokenOnLogOut();
            dataManager.updateSp(null);
            dataManager.deleteUserLocationPermission();
            ((MainActivity) requireActivity()).stopLocationService();
            Utils.moveBetweenFragmentsAndHideMenuBar(new openingFragment(), getActivity(), "opening");
        });
    }
}