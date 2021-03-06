package com.boss.centralbark_PostPc_2021;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class accountSettingFragment extends Fragment {
    private DataManager dataManager;
    private ImageView profileImage;
    private EditText userName;
    private EditText email;
    private EditText birthday;
    private EditText breed;
    private EditText livesIn;
    private EditText selfSummery;
    private Button saveChanges;
    private String fileType;
    private String picturePath = "not changed";
    private File image;
    private final int STORAGE_PERMISSION_CODE = 1;

    public accountSettingFragment(){
        super(R.layout.fragment_acount_setting);
        if(dataManager ==null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);

        this.profileImage = view.findViewById(R.id.profile_image_account_setting_screen);
        this.userName = view.findViewById(R.id.user_name_edit_text_account_setting_screen);
        this.email = view.findViewById(R.id.mail_edit_text_account_setting_screen);
        this.birthday = view.findViewById(R.id.birthday_edit_text_account_setting_screen);
        this.breed = view.findViewById(R.id.breed_edit_text_account_setting_screen);
        this.livesIn = view.findViewById(R.id.lives_in_edit_text_account_setting_screen);
        this.selfSummery = view.findViewById(R.id.my_self_summery_edit_text_account_setting_screen);
        this.saveChanges = view.findViewById(R.id.save_changes_button_account_setting_screen);

        // upload image from phone method:
        ActivityResultLauncher<Intent> upLoadLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        assert data != null;
                        Uri selectedImage = data.getData();
                        if (selectedImage == null)
                        {
                            Toast.makeText(getContext(), "Error uploading image", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            String[] filePathColumn = { MediaStore.Images.Media.DATA };

                            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            picturePath = cursor.getString(columnIndex);
                            String[] picturePathSplit = picturePath.split("\\.");
                            fileType = picturePathSplit[picturePathSplit.length-1];
                            image = new File(picturePath);
                            if(image.exists()){
                                profileImage.setImageURI(Uri.fromFile(image));
                            }
                            cursor.close();
                        }
                    }
                }
        );

        // Access user
        this.dataManager.db.collection("Users").document(this.dataManager.getMyId()).get().addOnSuccessListener(documentSnapshot -> {
            User myUser = documentSnapshot.toObject(User.class);

            // INITIALIZE ALL FIELDS WITH CURRENT INFO:

            // set profile picture
            assert myUser != null;
            if(!myUser.getProfilePhoto().equals("default")) {
                StorageReference profileImag = dataManager.storage.getReference().child(myUser.getProfilePhoto());
                File localProfileImFile = null;
                try {
                    localProfileImFile = File.createTempFile("profile_photos", "g");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File profileImFile = localProfileImFile;
                profileImag.getFile(profileImFile).addOnSuccessListener(
                        taskSnapshot -> profileImage.setImageURI(Uri.fromFile(profileImFile))).addOnFailureListener(
                                exception -> {
                            // keeps the default profile image
                        });
            }

            else{
                profileImage.setImageResource(R.drawable.default_dog);
            }


            // set user name
            userName.setText(myUser.getUsername());

            // set city
            livesIn.setText(myUser.getCity());

            // set birthday
            birthday.setText(myUser.getBirthday());

            // set email
            email.setText(myUser.getMail());
            email.setClickable(false);

            // set breed
            breed.setText(myUser.getBreed());

            // set self summery
            selfSummery.setText(myUser.getSelfSummary());

            // FINISH INITIALIZING ALL FIELDS WITH CURRENT INFO


            // upload new photo if profile image is clicked
            profileImage.setOnClickListener(v -> {
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
                                this.dataManager. setMediaPermission(false);
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

            // if save changes clicked - save the changes
            saveChanges.setOnClickListener(v -> {

                if (userName.getText().toString().equals(""))
                {
                    Toast.makeText(getContext(), "Username cannot be empty!", Toast.LENGTH_LONG).show();
                }

                else if (!Utils.isBirthdayValid(birthday.getText().toString()))
                {
                    Toast.makeText(getContext(), "Error: birthday format is MM/DD/YYYY", Toast.LENGTH_LONG).show();
                }

                else{
                    if (!email.getText().toString().equals("")){
                        ArrayList<User> users = new ArrayList<>();
                        Task<QuerySnapshot> result = dataManager.db.collection("Users").get();
                        result.addOnSuccessListener(documentSnapshots -> {
                            boolean is_mail_unique = true;
                            if (!documentSnapshots.isEmpty()) {
                                users.addAll(documentSnapshots.toObjects(User.class));
                                for (User user : users) {
                                    if (!user.getId().equals(myUser.getId())) {
                                        if (user.getMail().equals(email.getText().toString())) {
                                            is_mail_unique = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!is_mail_unique) {
                                Toast.makeText(getContext(), "The mail you chose is already in use!", Toast.LENGTH_LONG).show();
                            }
                            else{
                                if (!picturePath.equals("") && !picturePath.equals("not changed"))
                                {
                                    String remoteImgName = "profile_photos/" + myUser.getId()+"."+fileType;
                                    StorageReference storageReference = dataManager.storage.getReference();
                                    StorageReference imgRef = storageReference.child(remoteImgName);
                                    UploadTask uploadTask = imgRef.putFile(Uri.fromFile(new File(picturePath)));
                                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                                        myUser.setProfilePhoto(remoteImgName);
                                        dataManager.addToUsers(myUser);
                                    })
                                            .addOnFailureListener(e -> {
                                                myUser.setProfilePhoto("default");
                                                dataManager.addToUsers(myUser);
                                            });
                                }
                                myUser.setMail(email.getText().toString());
                                myUser.setUsername(userName.getText().toString());
                                myUser.setBirthday(birthday.getText().toString());
                                myUser.setBreed(breed.getText().toString());
                                myUser.setCity(livesIn.getText().toString());
                                myUser.setSelfSummary(selfSummery.getText().toString());

                                dataManager.updateSpWithUsername(myUser.getUsername());
                                dataManager.addToUsers(myUser);
                                Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_LONG).show();
                                Utils.moveBetweenFragments(R.id.the_screen, new settingsFragment(), getActivity(), "settings");

                            }
                        });
                    }
                    else{
                        Toast.makeText(getContext(), "Error: Email cannot be empty", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
    }
}