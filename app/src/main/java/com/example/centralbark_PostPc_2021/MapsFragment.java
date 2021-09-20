package com.example.centralbark_PostPc_2021;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;


public class MapsFragment extends Fragment implements OnMapReadyCallback {
    CentralBarkApp appInstance;
    FusedLocationProviderClient fusedLocationProviderClient;

    final String[] PERMISSIONS =
        {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        };

    final LatLng[] DOG_PARKS =
        {
            new LatLng(31.781896, 35.20541), // Sacher Park
            new LatLng(31.772408, 35.190774), // Ramat Beit Hakerem Park
            new LatLng(31.773485113624243, 35.21957354419318), // Sokolov Park
            new LatLng(31.762733966751608, 35.206619469755076), // San Simon Park
            new LatLng(31.757139379888653, 35.1673460059339), // Mexico Garden Park
            new LatLng(31.791138758045847, 35.19212324356424), // Zarchi Park
            new LatLng(31.756352613824877, 35.20847005180395), // Gonenim Park
        };

    ActivityResultContracts.RequestMultiplePermissions requestMultiplePermissionsContract;
    ActivityResultLauncher<String[]> multiplePermissionActivityResultLauncher;
    SupportMapFragment supportMapFragment;
    LatLng locationToZoomIn = null;

    public MapsFragment() {
    }

    public MapsFragment(LatLng location) {
        locationToZoomIn = location;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        if (appInstance == null) {
            appInstance = CentralBarkApp.getInstance();
        }
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
            }
        };

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            getCurrentLocation(locationToZoomIn);
        } else {
            multiplePermissionActivityResultLauncher.launch(PERMISSIONS);
        }

        addFriendsToMap();
        return view;
    }

    private void addFriendsToMap() {
        ArrayList<User> friendsList = new ArrayList<>();
        String userId = appInstance.getDataManager().getMyId();
        Task<DocumentSnapshot> result = appInstance.getDataManager().db.collection("Users").document(userId).get();
        result.addOnSuccessListener(documentSnapshot -> {
            ArrayList<String> friendsIds = (ArrayList<String>) documentSnapshot.get("friendList");
            if (friendsIds != null && friendsIds.size() > 0) {
                Task<QuerySnapshot> result1 = appInstance.getDataManager()
                        .db.collection("Users")
                        .whereIn("id", friendsIds).get();
                result1.addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                        friendsList.addAll(documentSnapshots.toObjects(User.class));
                        addMarkers(friendsList);
                    }
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error: db error", Toast.LENGTH_LONG).show());
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error: db error", Toast.LENGTH_LONG).show());
    }

    private void addMarkers(ArrayList<User> friendsList) {
        if (friendsList == null || friendsList.size() == 0) {
            return;
        }
        supportMapFragment.getMapAsync(googleMap -> {
            for (User user : friendsList) {
                LatLng curUserLocation = user.getLocationAsLatLng();
                if (curUserLocation != null) {
                    for (LatLng park : DOG_PARKS) {
                        if (Utils.isCloseToDogPark(park, curUserLocation, 200)) {
                            MarkerOptions options = new MarkerOptions().position(curUserLocation).title(user.getUsername());
                            googleMap.addMarker(options);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void getCurrentLocation(LatLng locationToZoomIn) {
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                supportMapFragment.getMapAsync(googleMap -> {
                    MarkerOptions options = new MarkerOptions().position(latLng);
                    if (locationToZoomIn == null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    } else {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationToZoomIn, 18));
                        options.position(locationToZoomIn).title(Utils.locationToNameMapping.get(locationToZoomIn));
                        googleMap.addMarker(options);
                    }


                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                });
            }
        });
    }


    private void askPermissions() {
        if (!hasPermissions(PERMISSIONS)) {
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