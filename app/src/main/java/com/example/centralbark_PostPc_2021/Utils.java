package com.example.centralbark_PostPc_2021;

import java.util.Arrays;
import java.util.List;

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

}
