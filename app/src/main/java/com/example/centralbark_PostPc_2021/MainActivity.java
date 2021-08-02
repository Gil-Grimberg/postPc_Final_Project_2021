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

        FragmentContainerView screen = findViewById(R.id.the_screen);

        openingFragment openingFragment = new openingFragment();
        if (dataManager==null)
        {
            dataManager = CentralBarkApp.getInstance().getDataManager();
        }
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().
                    beginTransaction().
                    replace(screen.getId(), openingFragment).
                    addToBackStack(null).
                    commit();
        }


//
//
//        if (dataManager==null)
//        {
//            dataManager = CentralBarkApp.getInstance().getDataManager();
//        }
//        Post myPost = new Post("jTAfJDj6c6nB5HjD3NyB","gil","profile_photo_link","uploaded_photo_link","this post was created in android",12,"07/19/21");
//
//        this.dataManager.addToPost(myPost);
    }
}