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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {

    final static List<String> VALID_MONTHS =
           new ArrayList<>(Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

    final static List<String> VALID_DAYS =
            new ArrayList<>(Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
            "29", "30", "31"));

    final static Map<LatLng, String> locationToNameMapping = ImmutableMap.<LatLng, String>builder()
            .put(new LatLng(31.781896, 35.20541), "Sacher Park")
            .put(new LatLng(31.772408, 35.190774), "Ramat Beit Hakerem Park")
            .put(new LatLng(31.773485113624243, 35.21957354419318), "Sokolov Park")
            .put(new LatLng(31.762733966751608, 35.206619469755076), "San Simon Park")
            .put(new LatLng(31.757139379888653, 35.1673460059339), "Mexico Garden Park")
            .put(new LatLng(31.791138758045847, 35.19212324356424), "Zarchi Park")
            .put(new LatLng(31.756352613824877, 35.20847005180395), "Gonenim Park")
            .build();

    final static LatLng[] DOG_PARKS =
            {
                    new LatLng(31.781896, 35.20541), // Sacher Park
                    new LatLng(31.772408, 35.190774), // Ramat Beit Hakerem Park
                    new LatLng(31.773485113624243, 35.21957354419318), // Sokolov Park
                    new LatLng(31.762733966751608, 35.206619469755076), // San Simon Park
                    new LatLng(31.757139379888653, 35.1673460059339), // Mexico Garden Park
                    new LatLng(31.791138758045847, 35.19212324356424), // Zarchi Park
                    new LatLng(31.756352613824877, 35.20847005180395), // Gonenim Park
            };

    static final String[] PERMISSIONS =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    static List<String> breeds = Arrays.asList("pitbull", "pug", "golden", "labrador", "german shepherd", "mixed");
    static List<String> cities = Arrays.asList("jerusalem", "rishon letzion", "tel aviv", "street dog");

    static final int LOCATION_SERVICE_ID = 175;
    static final int NOTIFICATION_SERVICE_ID = 176;
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
        if (breedAsString.equals(""))
        {
            return "mixed";
        }
        return breedAsString;
    }

    public static String parseCity(String cityAsString)
    {
        if (cityAsString.equals(""))
        {
            return "street dog";
        }
        else return cityAsString;
    }

    public static void moveBetweenFragments(int fragmentContainerViewId, Fragment fragment, FragmentActivity fragmentActivity, String fragmentTag)
    {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentContainerViewId, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void moveBetweenFragmentsAndHideMenuBar(Fragment fragment, FragmentActivity fragmentActivity, String fragmentTag)
    {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.the_screen, fragment, fragmentTag);
        fragmentTransaction.replace(R.id.menu_bar, new Fragment(), "menu");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static boolean isBirthdayValid(String birthday)
    {
        String[] dateParts = birthday.split("/");
        if (dateParts.length != 3)
        {
            return false;
        }
        for (String datePart : dateParts)
        {
            if (!datePart.matches("[0-9]+"))
            {
                return false;
            }
        }
        if (!VALID_MONTHS.contains(dateParts[1]) || !VALID_DAYS.contains(dateParts[0]))
        {
            return false;
        }
        return dateParts[0].length() == 2 && dateParts[1].length() == 2 && dateParts[2].length() == 4;
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

    public static float getTimeDifferenceInDays(Timestamp timestamp1, Timestamp timestamp2)
    {
        long seconds1 = timestamp1.getSeconds();
        long seconds2 = timestamp2.getSeconds();

        long diff = java.lang.Math.abs(seconds1 - seconds2);

        return (float) diff / 86400;
    }

    public static float getTimeDifferenceInHours(Timestamp timestamp1, Timestamp timestamp2)
    {
        long seconds1 = timestamp1.getSeconds();
        long seconds2 = timestamp2.getSeconds();

        long diff = java.lang.Math.abs(seconds1 - seconds2);

        return (float) diff / 3600;
    }

    public static float getTimestampsDifferenceInMinutes(Timestamp timestamp1, Timestamp timestamp2)
    {
        long seconds1 = timestamp1.getSeconds();
        long seconds2 = timestamp2.getSeconds();

        long diff = java.lang.Math.abs(seconds1 - seconds2);

        return (float) diff / 60;
    }
    public static void removeFriendsFromPosts(String curUserId, DataManager dataManager){
        // Access Posts in order to update the friends list
        Task<QuerySnapshot> result = dataManager.db.collection("Posts").get();
        result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (!documentSnapshots.isEmpty()) {
                    for(Post post: documentSnapshots.toObjects(Post.class)){
                        if(post.getUserId().equals(curUserId)){ // if it is his post, delete me from friend list
                            dataManager.addStringFromPostArrayField(post.getPostId(),"friendList",dataManager.getMyId());
                        }
                        else if(post.getUserId().equals(dataManager.getMyId())){ // if it is my post, delete him from my friend list
                            dataManager.addStringFromPostArrayField(post.getPostId(),"friendList",curUserId);
                        }
                    }
                }
            }
        });
    }



}
