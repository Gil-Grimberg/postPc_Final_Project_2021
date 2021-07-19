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
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link signUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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


        ActivityResultLauncher<Intent> upLoadLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()== Activity.RESULT_OK){
                            Intent data = result.getData();
                            Uri selectedImage = data.getData(); //todo: handle null case
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

        signUpButton.setOnClickListener();

    }

}