package com.example.centralbark_PostPc_2021;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LocationService extends Service {

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                Location myLocation = locationResult.getLastLocation();

                GeoPoint geoPoint = new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
                Log.d("LOCATION_UPDATE", geoPoint.toString());

                String userId = CentralBarkApp.getInstance().getDataManager().getMyId();

                CentralBarkApp.getInstance().getDataManager().updateUserLocation(userId, geoPoint);
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                for (LatLng park: Utils.DOG_PARKS)
                {
                    if (Utils.isCloseToDogPark(park, latLng, 200))
                    {
                        sendNotifications(latLng, park);
                    }
                }


            }
        }
    };

    private void sendNotifications(LatLng userLocation, LatLng park)
    {
        ArrayList<User> friendsList = new ArrayList<>();
        String userId = CentralBarkApp.getInstance().getDataManager().getMyId();
        Task<DocumentSnapshot> result = CentralBarkApp.getInstance().getDataManager().db.collection("Users").document(userId).get();
        result.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> friendsIds = (ArrayList<String>) documentSnapshot.get("friendList");
                if (friendsIds != null && friendsIds.size() > 0)
                {
                    Task<QuerySnapshot> result = CentralBarkApp.getInstance().getDataManager()
                            .db.collection("Users")
                            .whereIn("id", friendsIds).get();
                    result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            if (documentSnapshots != null && !documentSnapshots.isEmpty())
                            {
                                friendsList.addAll(documentSnapshots.toObjects(User.class));
                                for (User user: friendsList)
                                {
                                    ArrayList<Notification> friendNotifications = new ArrayList<>();
                                    Task<QuerySnapshot> result = CentralBarkApp.getInstance().getDataManager().db.collection("Users").document(user.getId()).collection("Notifications").get();
                                    result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot documentSnapshots)
                                        {
                                            boolean sent = false;
                                            if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                                                friendNotifications.addAll(documentSnapshots.toObjects(Notification.class));
                                                for (Notification notification : friendNotifications) {
                                                    float timeDiffInMinutes = Utils.getTimestampsDifferenceInMinutes(notification.getTimestamp(), Timestamp.now());
                                                    if (notification.getNotificationType() == NotificationTypes.USER_AT_THE_DOG_PARK_NOTIFICATION &&
                                                            notification.getUserId().equals(CentralBarkApp.getInstance().getDataManager().getMyId()) &&
                                                            timeDiffInMinutes < 60 &&
                                                            notification.getNotificationContent().contains(Utils.locationToNameMapping.get(park))) {
                                                        sent = true;
                                                        break;
                                                    }
                                                }
                                                if (!sent)
                                                {
                                                    CentralBarkApp.getInstance().getDataManager().sendNotification(NotificationTypes.USER_AT_THE_DOG_PARK_NOTIFICATION, user.getId(), "", Utils.locationToNameMapping.get(park));
                                                    CentralBarkApp.getInstance().getDataManager().sendFirebaseNotification("A User Entered a Dog Park!",
                                                            String.format("Your friend %s has entered %s", CentralBarkApp.getInstance().getDataManager().getUsernameFromSp(), Utils.locationToNameMapping.get(park)),
                                                                    user.getDeviceToken());
                                                }
                                            }
                                            else
                                            {
                                                CentralBarkApp.getInstance().getDataManager().sendNotification(NotificationTypes.USER_AT_THE_DOG_PARK_NOTIFICATION, user.getId(), "", Utils.locationToNameMapping.get(park));
                                                CentralBarkApp.getInstance().getDataManager().sendFirebaseNotification("A User Entered a Dog Park!",
                                                        String.format("Your friend %s has entered %s", CentralBarkApp.getInstance().getDataManager().getUsernameFromSp(), Utils.locationToNameMapping.get(park)),
                                                        user.getDeviceToken());
                                            }

                                        };
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error: db error", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: db error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: db error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
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
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location_Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setSound(null);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_LOW
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationChannel.setSound(null, null);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000); // update every 60 seconds
        locationRequest.setFastestInterval(30 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Utils.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService()
    {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopLocationService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
        {
            String action = intent.getAction();
            if (action != null)
            {
                if (action.equals(Utils.ACTION_START_LOCATION_SERVICE))
                {
                    startLocationService();
                }
                else if (action.equals(Utils.ACTION_STOP_LOCATION_SERVICE))
                {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
