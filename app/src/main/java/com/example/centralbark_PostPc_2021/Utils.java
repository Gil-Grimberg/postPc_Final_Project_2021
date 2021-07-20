package com.example.centralbark_PostPc_2021;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Arrays;
import java.util.List;

import io.grpc.Context;

public class Utils {


    static List<String> breeds = Arrays.asList("pitbull", "pug", "golden", "labrador", "german shepherd", "mixed");
    static List<String> cities = Arrays.asList("jerusalem", "rishon letzion", "tel aviv", "street dog");

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

    public static void moveBetweenFragments(int fragmentContainerViewId, Fragment fragment, FragmentActivity fragmentActivity)
    {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentContainerViewId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
