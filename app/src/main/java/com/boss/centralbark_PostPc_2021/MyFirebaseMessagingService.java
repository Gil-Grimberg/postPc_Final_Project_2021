package com.boss.centralbark_PostPc_2021;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.jetbrains.annotations.NotNull;


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
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), notification.getBody(), Toast.LENGTH_LONG).show());
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

        builder.setSmallIcon(R.drawable.app_logo_no_background);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_logo_no_background));
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(false);
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notification_sound);
        builder.setSound(soundUri);

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
        startForeground(Utils.NOTIFICATION_SERVICE_ID, builder.build());
    }
}
