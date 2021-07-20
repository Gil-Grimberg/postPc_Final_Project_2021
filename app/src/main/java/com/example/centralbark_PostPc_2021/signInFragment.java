package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class signInFragment extends Fragment {
    SignInViewModel signInViewModel = null;
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
        if (signInViewModel==null)
            signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        password = view.findViewById(R.id.password_edit_text_sign_in_screen);
        mail = view.findViewById(R.id.mail_edit_text_sign_in_screen);
        logIn = view.findViewById(R.id.log_me_in_button_sign_in_screen);
        rememberMe = view.findViewById(R.id.remember_me_checkbox_sign_in_screen);
        incorrectLoginInfo = view.findViewById(R.id.incorrect_login_data_textview_log_in_screen);

        password.setText(signInViewModel.password.getValue());
        mail.setText(signInViewModel.mail.getValue());

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                signInViewModel.password.setValue(password.getText().toString());

            }
        });

        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                signInViewModel.mail.setValue(mail.getText().toString());

            }
        });

        if (!signInViewModel.incorrectLogin.getValue())
            incorrectLoginInfo.setVisibility(View.INVISIBLE); // make the incorrect login warning invisible

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                signInViewModel.rememberMe.setValue(isChecked);
            }
        });

        rememberMe.setChecked(signInViewModel.rememberMe.getValue()); // make thr remember me value true as default

        logIn.setOnClickListener(v->{
            String enteredMail = signInViewModel.mail.getValue();
            String enteredPassword = signInViewModel.password.getValue();
            User enteredUser = findUser(enteredMail, enteredPassword);
            if(enteredUser == null){
                signInViewModel.incorrectLogin.setValue(true);

            }
            else{
                enteredUser.setRememberMe(signInViewModel.rememberMe.getValue());
                appInstance.getDataManager().updateSp(enteredUser.getId());
                Utils.moveBetweenFragments(R.id.the_screen, new feedFragment(),getActivity());
            }
        });

        signInViewModel.incorrectLogin.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                incorrectLoginInfo.setVisibility(View.VISIBLE);
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