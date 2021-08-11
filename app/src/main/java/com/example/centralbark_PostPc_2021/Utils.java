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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {


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
}
