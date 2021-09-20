package com.example.centralbark_PostPc_2021;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DataManager dataManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineNotificationChannel();
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

    void defineNotificationChannel()
    {
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notification_sound);
        String channelId = "central_app_notifications";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Central Bark",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                notificationChannel.setDescription("This channel is used by central bark app");
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                notificationChannel.setSound(soundUri, audioAttributes);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}