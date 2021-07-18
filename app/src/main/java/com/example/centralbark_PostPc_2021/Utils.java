package com.example.centralbark_PostPc_2021;

public class Utils {

    public static Breed convertStringToBreed(String breedAsString)
    {
        switch (breedAsString.toLowerCase())
        {
            case "pitbull":
                return Breed.PITBULL;
            case "pug":
                return Breed.PUG;
            case "golden":
                return Breed.GOLDEN;
            case "labrador":
                return Breed.LABRADOR;
            case "german shepherd":
                return Breed.GERMAN_SHEPHERD;
            default:
                return Breed.MIXED;
        }
    }

    public static City convertStringToCity(String cityAsString)
    {
        switch (cityAsString.toLowerCase())
        {
            case "jerusalem":
                return City.JERUSALEM;
            case "tel aviv":
                return City.TEL_AVIV;
            case "rishon lezion":
                return City.RISHON_LETZION;
            default:
                return City.STREET_DOG;
        }
    }

}
