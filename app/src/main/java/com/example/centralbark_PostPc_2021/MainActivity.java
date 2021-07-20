package com.example.centralbark_PostPc_2021;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;

import java.sql.Timestamp;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    DataManager dataManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (dataManager==null)
        {
            dataManager = CentralBarkApp.getInstance().getDataManager();
        }



        // -------------------------------Tests---------------------------------------------------//

    }
}