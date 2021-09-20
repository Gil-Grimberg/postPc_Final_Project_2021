package com.example.centralbark_PostPc_2021;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class aboutSettingFragment extends Fragment {
    private DataManager dataManager;

    public aboutSettingFragment() {
        super(R.layout.fragment_about_settings);
        if(dataManager ==null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);
    }
}
