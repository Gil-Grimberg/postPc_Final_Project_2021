package com.example.centralbark_PostPc_2021;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class centralBarkApp extends Application {
    private centralBarkApp instance  = null;
    private DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        instance = this;
        dataManager = new DataManager(this);
    }
    public static centralBarkApp getInstance(){ return instance; };

    public DataManager getDataManager() { return dataManager; }
}
