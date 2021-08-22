package com.example.centralbark_PostPc_2021;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DataManager dataManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentContainerView screen = findViewById(R.id.the_screen);
        FragmentContainerView menu = findViewById(R.id.menu_bar);
        openingFragment openingFragment = new openingFragment();
        MenuFragment menuFragment = new MenuFragment();
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

}