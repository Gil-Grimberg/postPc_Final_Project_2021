package com.example.centralbark_PostPc_2021;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class accountSettingFragment extends Fragment {
    private DataManager dataManager;
    private ImageView profileImage;
    private EditText userName;
    private EditText email;
    private EditText password;
    private EditText birthday;
    private EditText breed;
    private EditText livesIn;
    private EditText selfSummery;
    private Button saveChanges;
    private String fileType;
    private String picturePath = "not changed";
    private File image;

    public accountSettingFragment(){
        super(R.layout.fragment_acount_setting);
        if(dataManager ==null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);

        this.profileImage = view.findViewById(R.id.profile_image_account_setting_screen);
        this.userName = view.findViewById(R.id.user_name_title_account_setting_screen);
        this.email = view.findViewById(R.id.mail_title_account_setting_screen);
        this.password = view.findViewById(R.id.password_title_account_setting_screen);
        this.birthday = view.findViewById(R.id.birthday_title_account_setting_screen);
        this.breed = view.findViewById(R.id.breed_title_account_setting_screen);
        this.livesIn = view.findViewById(R.id.lives_in_title_account_setting_screen);
        this.selfSummery = view.findViewById(R.id.my_self_summery_edit_text_account_setting_screen);
        this.saveChanges = view.findViewById(R.id.save_changes_button_account_setting_screen);

        // Access user
        this.dataManager.db.collection("Users").document(this.dataManager.getMyId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User myUser = documentSnapshot.toObject(User.class);

                // INITIALIZE ALL FIELDS WITH CURRENT INFO:

                // set profile picture
                StorageReference profileImag = dataManager.storage.getReference().child(myUser.getProfilePhoto());
                File localProfileImFile = null;
                try {
                    localProfileImFile = File.createTempFile("profile_photos", "g");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File profileImFile = localProfileImFile;
                profileImag.getFile(profileImFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        profileImage.setImageURI(Uri.fromFile(profileImFile));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // keeps the default profile image
                    }
                });

                // set user name
                userName.setText(myUser.getUsername());

                // set city
                livesIn.setText(myUser.getCity());

                // set birthday
                birthday.setText(myUser.getBirthday());

                // set email
                email.setText(myUser.getMail());
                email.setClickable(false);

                // set password
                password.setText(myUser.getPassword());

                // set breed
                breed.setText(myUser.getBreed());

                // set self summery
                selfSummery.setText(myUser.getSelfSummary());

                // FINISH INITIALIZING ALL FIELDS WITH CURRENT INFO

                // upload image from phone method:
                ActivityResultLauncher<Intent> upLoadLauncher = registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if(result.getResultCode()== Activity.RESULT_OK){
                                    Intent data = result.getData();
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
                        }
                );

                // upload new photo if profile image is clicked
                profileImage.setOnClickListener(v -> {
                    Intent uploadIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    upLoadLauncher.launch(uploadIntent);
                });

                // if save changes clicked - save the changes
                saveChanges.setOnClickListener(v -> {
                    if (password.getText().toString().equals(""))
                    {
                        Toast.makeText(getContext(), "Password cannot be empty!", Toast.LENGTH_LONG).show();
                    }

                    else if (password.getText().toString().length() != 8)
                    {
                        Toast.makeText(getContext(), "Password must contain 8 chars!", Toast.LENGTH_LONG).show();
                    }

                    else if (userName.getText().toString().equals(""))
                    {
                        Toast.makeText(getContext(), "Username cannot be empty!", Toast.LENGTH_LONG).show();
                    }

                    else if (!Utils.isBirthdayValid(birthday.getText().toString()))
                    {
                        Toast.makeText(getContext(), "Error: birthday format is MM/DD/YYYY", Toast.LENGTH_LONG).show();
                    }
                    else{
                        myUser.setUsername(userName.getText().toString());
                        myUser.setPassword(password.getText().toString());
                        myUser.setBirthday(birthday.getText().toString());
                        myUser.setBreed(breed.getText().toString());
                        myUser.setCity(livesIn.getText().toString());
                        myUser.setSelfSummary(selfSummery.getText().toString());
                        if (!picturePath.equals("") && !picturePath.equals("not changed"))
                        {
                            String remoteImgName = "profile_photos/" + myUser.getId()+"."+fileType;
                            StorageReference storageReference = dataManager.storage.getReference();
                            StorageReference imgRef = storageReference.child(remoteImgName);
                            UploadTask uploadTask = imgRef.putFile(Uri.fromFile(new File(picturePath)));
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    myUser.setProfilePhoto(remoteImgName);
                                }
                            })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        myUser.setProfilePhoto("default");
                                    }
                                });
                        }
                        dataManager.updateSpForSignIn(myUser.getId(),myUser.getMail(), myUser.getPassword());
                        dataManager.updateSpWithUsername(myUser.getUsername());
                        dataManager.addToUsers(myUser);
                        Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_LONG).show();
                        Utils.moveBetweenFragments(R.id.the_screen, new settingsFragment(), getActivity(), "settings");
                    }

                });

            }
        });
    }
}