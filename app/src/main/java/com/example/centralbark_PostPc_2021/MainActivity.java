package com.example.centralbark_PostPc_2021;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.sql.Timestamp;

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
        Post myPost = new Post("jTAfJDj6c6nB5HjD3NyB","gil","profile_photo_link","uploaded_photo_link","this post was created in android",12,"07/19/21");

        this.dataManager.addToPost(myPost);
    }
}