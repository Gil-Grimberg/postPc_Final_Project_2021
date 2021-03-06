package com.boss.centralbark_PostPc_2021;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AddPostFragment extends Fragment {
    private DataManager dataManager;
    private ImageView uploadIm;
    private EditText caption;
    private Button uploadPostButton;
    String fileType;
    String picturePath = null;
    File image;
    User myUser;
    private final int STORAGE_PERMISSION_CODE = 1;

    public AddPostFragment() {
        super(R.layout.fragment_add_post);
        if (dataManager == null) {
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.uploadIm = view.findViewById(R.id.upload_im_add_post_screen);
        this.caption = view.findViewById(R.id.caption_text_add_post_screen);
        this.uploadPostButton = view.findViewById(R.id.upload_im_button_add_post_screen);

        // upload image from phone method:
        ActivityResultLauncher<Intent> upLoadLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri selectedImage = data.getData();
                        if (selectedImage == null) {
                            Toast.makeText(getContext(), "Error uploading image", Toast.LENGTH_LONG).show();
                        } else {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            picturePath = cursor.getString(columnIndex);
                            String[] picturePathSplit = picturePath.split("\\.");
                            fileType = picturePathSplit[picturePathSplit.length - 1];
                            image = new File(picturePath);
                            if (image.exists()) {
                                uploadIm.setImageURI(Uri.fromFile(image));
                            }
                            cursor.close();
                        }
                    }
                }
        );

        // upload photo button
        this.uploadIm.setOnClickListener(v -> {
            try{
                if(this.dataManager.isSpContainsMediaPermission())
                {
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(!this.dataManager.isMediaPermissionGranted())
                            this.dataManager.setMediaPermission(true);
                        Intent uploadIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        upLoadLauncher.launch(uploadIntent);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Media access permission denied. If you want to allow it," +
                                " do it in your phone settings.", Toast.LENGTH_LONG).show();
                        if (this.dataManager.isMediaPermissionGranted())
                        {
                            this.dataManager.setMediaPermission(false);
                        }
                    }

                }
                else
                {
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        this.dataManager.setMediaPermission(true);
                        Intent uploadIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        upLoadLauncher.launch(uploadIntent);
                    }
                    else
                    {
                        this.dataManager.setMediaPermission(false);
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    }
                }

            }
            catch (Exception e) {
                Toast.makeText(getContext(), "problem with media permissions", Toast.LENGTH_LONG).show();
            }

        });


        // upload post button
        this.uploadPostButton.setOnClickListener(v -> {
            if (picturePath == null) {
                Toast.makeText(getContext(), "You need to chose an image to post!", Toast.LENGTH_LONG).show();
            } else {
                String myId = this.dataManager.getMyId();
                this.dataManager.db.collection("Users").document(myId).get().addOnSuccessListener(documentSnapshot -> {
                    myUser = documentSnapshot.toObject(User.class);
                    String postId = UUID.randomUUID().toString();
                    String postPath = "post_photos/" + postId + "." + fileType;
                    String content = caption.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String curDateString = sdf.format(new Date());

                    StorageReference storageReference = dataManager.storage.getReference();
                    StorageReference imgRef = storageReference.child(postPath);
                    UploadTask uploadTask = imgRef.putFile(Uri.fromFile(new File(picturePath)));
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        Post curPost = new Post(myId, postId, myUser.getUsername(), myUser.getProfilePhoto(), postPath, content, 0, curDateString, myUser.getFriendList());
                        dataManager.addToPost(curPost);
                        Utils.moveBetweenFragments(R.id.the_screen, new FeedFragment(), getActivity(), "feed");

                    })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Could not upload post, try again", Toast.LENGTH_LONG).show());
                });
            }
        });
    }
}
