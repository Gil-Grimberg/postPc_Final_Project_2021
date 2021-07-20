package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class signInFragment extends Fragment {
    CentralBarkApp appInstance;
    EditText password;
    EditText mail;
    Button logIn;
    CheckBox rememberMe;
    TextView incorrectLoginInfo;

    public signInFragment() {
        super(R.layout.fragment_sign_in);
        if(appInstance==null){
            appInstance = CentralBarkApp.getInstance();
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);
        password = view.findViewById(R.id.password_edit_text_sign_in_screen);
        mail = view.findViewById(R.id.mail_edit_text_sign_in_screen);
        logIn = view.findViewById(R.id.log_me_in_button_sign_in_screen);
        rememberMe = view.findViewById(R.id.remember_me_checkbox_sign_in_screen);
        incorrectLoginInfo = view.findViewById(R.id.incorrect_login_data_textview_log_in_screen);

        incorrectLoginInfo.setVisibility(View.INVISIBLE); // make the incorrect login warning invisible
        rememberMe.setChecked(true); // make thr remember me value true as default

        logIn.setOnClickListener(v->{
            String enteredMail = mail.getText().toString();
            String enteredPassword = password.getText().toString();
            User enteredUser = findUser(enteredMail, enteredPassword);
            if(enteredUser == null){
                incorrectLoginInfo.setVisibility(View.VISIBLE);
            }
            else{
                enteredUser.setRememberMe(rememberMe.isChecked());
                appInstance.getDataManager().updateSp(enteredUser.getId());
                Utils.moveBetweenFragments(R.id.the_screen, new FeedFragment(),getActivity(), "feed");
            }
        });
    }

    public User findUser(String enteredMail, String enteredPassword){
        ArrayList<User> users = this.appInstance.getDataManager().getAllUsers();
        for (User user: users){
            if (user.getMail().equals(enteredMail) && user.getPassword().equals(enteredPassword)){
                return user;
            }
        }
        return null;
    }
}