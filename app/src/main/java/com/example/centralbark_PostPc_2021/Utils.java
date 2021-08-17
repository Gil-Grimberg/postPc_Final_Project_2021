package com.example.centralbark_PostPc_2021;

import android.Manifest;
import android.app.Activity;
import android.location.Location;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.Timestamp;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {


    final static Map<LatLng, String> locationToNameMapping = ImmutableMap.of(
            new LatLng(31.781896, 35.20541), "Sacher park",
            new LatLng(31.772408, 35.190774), "Ramat Beit Hakerem Park",
            new LatLng(37.4219983, -122.084), "test" //todo remove this pair after testing
    );

    final static LatLng[] DOG_PARKS =
            {
                    new LatLng(31.781896, 35.20541), // Sacher park
                    new LatLng(31.772408, 35.190774), // Ramat Beit Hakerem Park
                    new LatLng(37.4219983, -122.084) // test
            };

    static final String[] PERMISSIONS =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    static List<String> breeds = Arrays.asList("pitbull", "pug", "golden", "labrador", "german shepherd", "mixed");
    static List<String> cities = Arrays.asList("jerusalem", "rishon letzion", "tel aviv", "street dog");

    static final int LOCATION_SERVICE_ID = 175;
    static final String ACTION_START_LOCATION_SERVICE = "startLocationService";
    static final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";

    public static List<String> getBreeds() {
        return breeds;
    }

    public List<String> getCities() {
        return cities;
    }

    public static String parseBreed(String breedAsString)
    {
        if (breeds.contains(breedAsString.toLowerCase()))
        {
            return breedAsString.toLowerCase();
        }
        return "mixed";
    }

    public static String parseCity(String cityAsString)
    {
        if (cities.contains(cityAsString.toLowerCase()))
        {
            return cityAsString.toLowerCase();
        }
        return "street dog";
    }

    public static void moveBetweenFragments(int fragmentContainerViewId, Fragment fragment, FragmentActivity fragmentActivity, String fragmentTag)
    {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentContainerViewId, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static float calculateDistanceBetweenPoints(LatLng point1, LatLng point2)
    /***
     * returns the distance in Meters between two points
     */
    {
        Location location1 = new Location("");
        Location location2 = new Location("");

        location1.setLatitude(point1.latitude);
        location1.setLongitude(point1.longitude);

        location2.setLatitude(point2.latitude);
        location2.setLongitude(point2.longitude);

        return location1.distanceTo(location2);
    }

    public static boolean isCloseToDogPark(LatLng dogPark, LatLng point, int threshold)
    {
        return (calculateDistanceBetweenPoints(dogPark, point) <= threshold);
    }

    @Nullable
    public static String getNotificationContent(int notificationType, String username, String dogPark)
    {
        switch (notificationType)
        {
            case NotificationTypes.USER_AT_THE_DOG_PARK_NOTIFICATION:
                return String.format("%s has entered %s", username, dogPark);

            case NotificationTypes.USER_LIKED_YOUR_POST_NOTIFICATION:
                return String.format("%s liked your post!", username);

            case NotificationTypes.TINDER_MATCH_NOTIFICATION:
                return String.format("You have a match with %s!", username);

            case NotificationTypes.FRIEND_REQUEST_ACCEPTED_NOTIFICATION:
                return String.format("%s has accepted your friend request :)", username);

            case NotificationTypes.FRIEND_REQUEST_RECEIVED_NOTIFICATION:
                return String.format("%s wants to be your friend!", username);

            default:
                return null;
        }
    };

    public static float getTimestampsDifferenceInMinutes(Timestamp timestamp1, Timestamp timestamp2)
    {
        long seconds1 = timestamp1.getSeconds();
        long seconds2 = timestamp2.getSeconds();

        long diff = java.lang.Math.abs(seconds1 - seconds2);

        return (float) diff / 60;
    }

}
