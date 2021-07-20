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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class signUpFragment extends Fragment {
    CentralBarkApp appInstance;
    EditText userName;
    EditText password;
    EditText mail;
    EditText birthday;
    EditText breed;
    EditText city;
    EditText selfSummary;
    Button uploadPhotoButton;
    Button signUpButton;
    TextView imagePath;


    public signUpFragment() {
        super(R.layout.fragment_sign_up);
        if(appInstance==null){
            appInstance = CentralBarkApp.getInstance();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userName = view.findViewById(R.id.user_name_edit_text_sign_up_screen);
        password =  view.findViewById(R.id.password_edit_text_sign_up);
        mail = view.findViewById(R.id.mail_edit_text_sign_up_screen);
        birthday = view.findViewById(R.id.birthday_edit_text_sign_up_screen);
        breed = view.findViewById(R.id.breed_edit_text_sign_up_screen);
        city = view.findViewById(R.id.lives_in_edit_text_sign_up_screen);
        selfSummary = view.findViewById(R.id.my_self_summery_edit_text_sign_up_screen);
        uploadPhotoButton = view.findViewById(R.id.choose_photo_button_sign_up_screen);
        signUpButton = view.findViewById(R.id.sign_me_up_button);
        imagePath = view.findViewById(R.id.image_path);


//        if (savedInstanceState != null)
//        {
//            userName.setText(savedInstanceState.getString("username"));
//            password.setText(savedInstanceState.getString("password"));
//            mail.setText(savedInstanceState.getString("mail"));
//            birthday.setText(savedInstanceState.getString("birthday"));
//            breed.setText(savedInstanceState.getString("breed"));
//            city.setText(savedInstanceState.getString("city"));
//            selfSummary.setText(savedInstanceState.getString("self_summary"));
//            imagePath.setText(savedInstanceState.getString("image_path"));
//        }

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
                                imagePath.setText(picturePath);
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
            if (!isMailUnique(mail.getText().toString()))
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

            else if (userName.getText().toString().equals(""))
            {
                Toast.makeText(getContext(), "Username cannot be empty!", Toast.LENGTH_LONG).show();
            }

            else if (!isBirthdayValid(birthday.getText().toString()))
            {
                Toast.makeText(getContext(), "Error: birthday format is MM/DD/YYYY", Toast.LENGTH_LONG).show();
            }
            else
            {
                if (imagePath.getText().toString().equals(""))
                {
                    //todo:: put default profile picture
                }

                User newUser = new User(userName.getText().toString(),
                                        password.getText().toString(),
                                        mail.getText().toString(),
                                        "",
                                        birthday.getText().toString(),
                                        breed.getText().toString(),
                                        city.getText().toString(),
                                        true,
                                        selfSummary.getText().toString());
                if (imagePath.getText().toString().equals(""))
                {

                }
                else
                {
                    String remoteImgName = "profile_photos/" + newUser.getId();
                    String downloadUrl = this.appInstance.getDataManager().uploadImgToStorageAndGetImgPath(imagePath.getText().toString(), remoteImgName);
                    newUser.setPhoto(downloadUrl);
                }


                this.appInstance.getDataManager().updateSp(newUser.getId());
                this.appInstance.getDataManager().addToUsers(newUser);

                Utils.moveBetweenFragments(R.id.the_screen, new feedFragment(),getActivity());

            }

        });
    }

    private boolean isMailUnique(String mail)
    {
        ArrayList<User> users = this.appInstance.getDataManager().getAllUsers();
        for (User user: users)
        {
            if (user.getMail().equals(mail))
            {
                return false;
            }
        }
        return true;
    }

    private boolean isBirthdayValid(String birthday)
    {
        String[] dateParts = birthday.split("/");
        if (dateParts.length != 3)
        {
            return false;
        }
        for (String datePart : dateParts)
        {
            if (!datePart.matches("[0-9]+"))
            {
                return false;
            }
        }
        return dateParts[0].length() == 2 && dateParts[1].length() == 2 && dateParts[2].length() == 4;
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username", this.userName.getText().toString());
        outState.putString("password", this.password.getText().toString());
        outState.putString("mail", this.mail.getText().toString());
        outState.putString("birthday", this.birthday.getText().toString());
        outState.putString("breed", this.breed.getText().toString());
        outState.putString("city", this.breed.getText().toString());
        outState.putString("self_summary", this.selfSummary.getText().toString());
        outState.putString("image_path", this.imagePath.getText().toString());
    }
}