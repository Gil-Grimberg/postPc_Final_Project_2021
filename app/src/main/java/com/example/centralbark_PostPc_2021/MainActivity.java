package com.example.centralbark_PostPc_2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    DataManager dataManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        FragmentContainerView screen = findViewById(R.id.the_screen);
        FragmentContainerView menu = findViewById(R.id.menu_bar);
        openingFragment openingFragment = new openingFragment();
        if (dataManager==null)
        {
            dataManager = CentralBarkApp.getInstance().getDataManager();
        }
//        String[] userInfo = dataManager.getInfoForSignIn();
//        if (userInfo != null)
//        {
//            getSupportFragmentManager().
//                    beginTransaction().
//                    replace(screen.getId(), new FeedFragment())
//                    .addToBackStack(null)
//                    .replace(menu.getId(), new menuFragment())
//                    .addToBackStack(null)
//                    .commit();
//        }
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


    private boolean isLocationServiceRunning()
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null)
        {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE))
            {
                if (LocationService.class.getName().equals(service.service.getClassName()))
                {
                    if (service.foreground)
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    public void startLocationService()
    {
        if (!isLocationServiceRunning())
        {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Utils.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }


    public void stopLocationService()
    {
        if (isLocationServiceRunning())
        {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Utils.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CentralBarkApp.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CentralBarkApp.activityPaused();
    }

}