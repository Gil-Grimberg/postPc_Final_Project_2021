package com.example.centralbark_PostPc_2021;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class FeedFragment extends Fragment {
    private DataManager dataManager;
    private ImageView notification;
    private RecyclerView recyclerViewPosts;

    ActivityResultContracts.RequestMultiplePermissions requestMultiplePermissionsContract;
    ActivityResultLauncher<String[]> multiplePermissionActivityResultLauncher;

    public FeedFragment() {
        super(R.layout.fragment_feed);
        if(dataManager ==null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

        //first, we activate the location service, so we can update the user location in the db:
        //to do that, we first need to ask location permissions from the user:
        requestMultiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionActivityResultLauncher = registerForActivityResult(requestMultiplePermissionsContract, isGranted ->
        {
            Log.d("PERMISSIONS", "Launcher result: " + isGranted.toString());
            if (isGranted.containsValue(false)) {
                Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
                multiplePermissionActivityResultLauncher.launch(Utils.PERMISSIONS);
            }
        });
        askPermissions(); // this will cause a loop until the user agrees :)

        // now we can start the location service:
        ((MainActivity) requireActivity()).startLocationService();


        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);
        notification = view.findViewById(R.id.notification_button_feed_screen);
        recyclerViewPosts = view.findViewById(R.id.post_recyclerview_feed_screen);


    }

    private void askPermissions() {
        if (!hasPermissions(Utils.PERMISSIONS))
        {
            multiplePermissionActivityResultLauncher.launch(Utils.PERMISSIONS);
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

//    public int findAmountOfPosts(){
//        User myUser = this.dataManager.getUserById(this.dataManager.getMyId());
//        ArrayList<Post> allRelevantPosts = new ArrayList<>();
//        allRelevantPosts.addAll(this.dataManager.getPostsById(myUser.getId())); // add all my posts
//        for(String friendId: myUser.getFriendList()){
//            allRelevantPosts.addAll(this.dataManager.getPostsById(friendId)); // add all my friends posts
//        }
//        // todo: sort allRelevantPosts by time (comparator in Post) and show only the X relevant
//    }
}