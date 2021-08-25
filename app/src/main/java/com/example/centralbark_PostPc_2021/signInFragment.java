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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

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
        if(appInstance == null){
            appInstance = CentralBarkApp.getInstance();
        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);
        password = view.findViewById(R.id.password_edit_text_sign_in_screen);
        mail = view.findViewById(R.id.mail_edit_text_sign_in_screen);
        logIn = view.findViewById(R.id.log_me_in_button_sign_in_screen);
        rememberMe = view.findViewById(R.id.remember_me_checkbox_sign_in_screen);
        incorrectLoginInfo = view.findViewById(R.id.incorrect_login_data_textview_log_in_screen);

        incorrectLoginInfo.setVisibility(View.INVISIBLE); // make the incorrect login warning invisible
        rememberMe.setChecked(true); // make thr remember me value true as default

        String[] userInfo = appInstance.getDataManager().getInfoForSignIn();
        if (userInfo != null)
        {
            mail.setText(userInfo[0]);
            password.setText(userInfo[1]);
        }

        logIn.setOnClickListener(v->{
            String enteredMail = mail.getText().toString();
            String enteredPassword = password.getText().toString();
            ArrayList<User> users = new ArrayList<>();
            Task<QuerySnapshot> result = this.appInstance.getDataManager().db.collection("Users").get();
            result.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    User enteredUser = null;
                    if (!documentSnapshots.isEmpty())
                    {
                        users.addAll(documentSnapshots.toObjects(User.class));
                        for (User user: users)
                        {
                            if (!user.getUsername().equals("treeUser") && user.getMail().equals(enteredMail) && user.getPassword().equals(enteredPassword))
                            {
                                enteredUser = user;
                                break;
                            }
                        }
                    }
                    if(enteredUser == null){
                        incorrectLoginInfo.setVisibility(View.VISIBLE);
                    }
                    else{
                        enteredUser.setRememberMe(rememberMe.isChecked());
                        if (rememberMe.isChecked())
                        {
                            appInstance.getDataManager().updateSpForSignIn(enteredUser.getId(), enteredMail, enteredPassword);
                        }
                        else
                        {
                            appInstance.getDataManager().updateSp(enteredUser.getId());
                            appInstance.getDataManager().deleteSignInInfoFromSp();
                        }
                        FirebaseMessaging.getInstance().getToken()
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        appInstance.getDataManager().updateDeviceToken(appInstance.getDataManager().getMyId());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                appInstance.getDataManager().updateDeviceToken(null);
                            }
                        });
                        appInstance.getDataManager().updateSpWithUsername(enteredUser.getUsername());
                        Utils.moveBetweenFragments(R.id.the_screen, new FeedFragment(), getActivity(), "feed");
                        Utils.moveBetweenFragments(R.id.menu_bar, new menuFragment(), getActivity(), "menu");
                    }
        }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Error: couldn't connect to database", Toast.LENGTH_LONG).show();
                }
            });
    });};};
