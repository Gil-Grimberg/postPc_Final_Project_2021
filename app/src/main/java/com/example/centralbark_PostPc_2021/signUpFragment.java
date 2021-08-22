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
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;


public class signUpFragment extends Fragment {
    CentralBarkApp appInstance;
    EditText userName;
    EditText password;
    EditText repeatPassword;
    EditText mail;
    EditText birthday;
    EditText breed;
    EditText city;
    EditText selfSummary;
    Button uploadPhotoButton;
    Button signUpButton;
    String imagePath = "";
    TextView imageName;
    String fileType;


    public signUpFragment() {
        super(R.layout.fragment_sign_up);
        if(appInstance==null){
            appInstance = CentralBarkApp.getInstance();
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userName = view.findViewById(R.id.user_name_edit_text_sign_up_screen);
        password =  view.findViewById(R.id.password_edit_text_sign_up);
        repeatPassword = view.findViewById(R.id.repeat_new_password_editText_sign_up_screen);
        mail = view.findViewById(R.id.mail_edit_text_sign_up_screen);
        birthday = view.findViewById(R.id.birthday_edit_text_sign_up_screen);
        breed = view.findViewById(R.id.breed_edit_text_sign_up_screen);
        city = view.findViewById(R.id.lives_in_edit_text_sign_up_screen);
        selfSummary = view.findViewById(R.id.my_self_summery_edit_text_sign_up_screen);
        uploadPhotoButton = view.findViewById(R.id.choose_photo_button_sign_up_screen);
        signUpButton = view.findViewById(R.id.sign_me_up_button);
        imageName = view.findViewById(R.id.image_name);

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
                                String picturePath = cursor.getString(columnIndex);
                                String[] picturePathSplit = picturePath.split("\\.");
                                fileType = picturePathSplit[picturePathSplit.length-1];
                                imageName.setText("Uploaded successfully");
                                imagePath = picturePath;
                                cursor.close();
                            }
                        }
                    }
                }
        );


        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                upLoadLauncher.launch(uploadIntent);
            }
        });

        signUpButton.setOnClickListener(v ->
        {
            ArrayList<User> users = new ArrayList<>();
            Task<QuerySnapshot> result = this.appInstance.getDataManager().db.collection("Users").get();
            result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    boolean is_mail_unique = true;
                    if (!documentSnapshots.isEmpty())
                    {
                        users.addAll(documentSnapshots.toObjects(User.class));
                        for (User user: users)
                        {
                            if (user.getMail().equals(mail.getText().toString()))
                            {
                                is_mail_unique = false;
                                break;
                            }
                        }
                    }
                    if (!is_mail_unique)
                    {
                        Toast.makeText(getContext(), "The mail you chose is already in use!", Toast.LENGTH_LONG).show();
                    }
                    else if (password.getText().toString().equals(""))
                    {
                        Toast.makeText(getContext(), "Password cannot be empty!", Toast.LENGTH_LONG).show();
                    }

                    else if (password.getText().toString().length() != 8)
                    {
                        Toast.makeText(getContext(), "Password must contain 8 chars!", Toast.LENGTH_LONG).show();
                    }
                    else if(!password.getText().toString().equals(repeatPassword.getText().toString()))
                    {
                        Toast.makeText(getContext(), "Inconsistent passwords", Toast.LENGTH_LONG).show();
                    }

                    else if (userName.getText().toString().equals(""))
                    {
                        Toast.makeText(getContext(), "Username cannot be empty!", Toast.LENGTH_LONG).show();
                    }

                    else if (!Utils.isBirthdayValid(birthday.getText().toString()))
                    {
                        Toast.makeText(getContext(), "Error: birthday format is MM/DD/YYYY", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        User newUser = new User(userName.getText().toString(),
                                password.getText().toString(),
                                mail.getText().toString(),
                                "",
                                birthday.getText().toString(),
                                breed.getText().toString(),
                                city.getText().toString(),
                                true,
                                selfSummary.getText().toString(),
                                null);
                        appInstance.getDataManager().updateSpWithUsername(newUser.getUsername());
                        if (imagePath.equals(""))
                        {
                            newUser.setProfilePhoto("default");
                            appInstance.getDataManager().updateSp(newUser.getId());
                            appInstance.getDataManager().deleteSignInInfoFromSp();
                            appInstance.getDataManager().addToUsers(newUser);
                            Utils.moveBetweenFragments(R.id.the_screen, new FeedFragment(), getActivity(), "feed");
                            Utils.moveBetweenFragments(R.id.menu_bar, new MenuFragment(), getActivity(), "menu");
                        }
                        else
                        {
                            String remoteImgName = "profile_photos/" + newUser.getId()+"."+fileType;
                            StorageReference storageReference = appInstance.getDataManager().storage.getReference();
                            StorageReference imgRef = storageReference.child(remoteImgName);
                            UploadTask uploadTask = imgRef.putFile(Uri.fromFile(new File(imagePath)));
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    newUser.setProfilePhoto(remoteImgName);
                                    appInstance.getDataManager().updateSp(newUser.getId());
                                    appInstance.getDataManager().deleteSignInInfoFromSp();
                                    appInstance.getDataManager().addToUsers(newUser);
                                    Utils.moveBetweenFragments(R.id.the_screen, new FeedFragment(), getActivity(), "feed");
                                    Utils.moveBetweenFragments(R.id.menu_bar, new MenuFragment(), getActivity(), "menu");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    newUser.setProfilePhoto("default");
                                    appInstance.getDataManager().updateSp(newUser.getId());
                                    appInstance.getDataManager().deleteSignInInfoFromSp();
                                    appInstance.getDataManager().addToUsers(newUser);
                                    Utils.moveBetweenFragments(R.id.the_screen, new FeedFragment(), getActivity(), "feed");
                                    Utils.moveBetweenFragments(R.id.menu_bar, new MenuFragment(), getActivity(), "menu");
                                }
                            });
                        }
                }
            }});
        });
    }
}