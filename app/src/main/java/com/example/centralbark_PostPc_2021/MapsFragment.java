package com.example.centralbark_PostPc_2021;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    FusedLocationProviderClient fusedLocationProviderClient;

    final String[] PERMISSIONS =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    final LatLng[] DOG_PARKS =
            {
                    new LatLng(31.781896, 35.20541), // Sacher park
                    new LatLng(31.772408, 35.190774) // Ramat Beit Hakerem Park
            };

    ActivityResultContracts.RequestMultiplePermissions requestMultiplePermissionsContract;
    ActivityResultLauncher<String[]> multiplePermissionActivityResultLauncher;
    SupportMapFragment supportMapFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        requestMultiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionActivityResultLauncher = registerForActivityResult(requestMultiplePermissionsContract, isGranted ->
        {
            Log.d("PERMISSIONS", "Launcher result: " + isGranted.toString());
            if (isGranted.containsValue(false)) {
                Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
                multiplePermissionActivityResultLauncher.launch(PERMISSIONS);
            }
        });

        askPermissions();
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            getCurrentLocation();
        }
        else
        {
            multiplePermissionActivityResultLauncher.launch(PERMISSIONS);
        }
        return view;
    }

    private void getCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                 if (location != null)
                 {
                     supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                         @Override
                         public void onMapReady(GoogleMap googleMap) {
                             LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                             MarkerOptions options = new MarkerOptions().position(latLng).title("I am Here");
                             googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                             googleMap.addMarker(options);
                         }
                     });
                 }
            }
        });

    }

    private void askPermissions() {
        if (!hasPermissions(PERMISSIONS))
        {
            multiplePermissionActivityResultLauncher.launch(PERMISSIONS);
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}