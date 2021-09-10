package com.example.centralbark_PostPc_2021;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        super.onNewToken(token);
        CentralBarkApp.getInstance().getDataManager().updateDeviceToken(token);
        Log.d("Tag", "the token refreshed: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification == null || notification.getTitle() == null || notification.getBody() == null)
        {
            return;
        }
        if (CentralBarkApp.isActivityVisible())
        {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), notification.getBody(), Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        String title = notification.getTitle();
        String body = notification.getBody();
        String channelId = "central_app_notifications";
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.drawable.app_icon);  //  TODO: define our logo here
        builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.app_icon));
        builder.setContentTitle(title);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText(body);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setOngoing(true);
//        builder.setSound();  TODO: define here the sound that we want

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Central Bark",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by central bark app");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
//        startForeground(Utils.NOTIFICATION_SERVICE_ID, builder.build());
        notificationManager.notify(Utils.NOTIFICATION_SERVICE_ID, builder.build());

    }

}
