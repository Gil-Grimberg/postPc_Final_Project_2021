package com.boss.centralbark_PostPc_2021;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.jetbrains.annotations.NotNull;

public class passwordSettingsFragment extends Fragment {
    private DataManager dataManager;
    private EditText currentPassword;
    private EditText newPassword;
    private EditText repeatPassword;
    private Button saveChanges;

    public passwordSettingsFragment() {
        super(R.layout.fragment_password_setting);
        if(dataManager ==null){
            this.dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);

        this.currentPassword = view.findViewById(R.id.curr_password_editText_password_settings_screen);
        this.newPassword = view.findViewById(R.id.new_password_editText_password_settings_screen);
        this.repeatPassword = view.findViewById(R.id.repeat_new_password_editText_password_settings_screen);
        this.saveChanges = view.findViewById(R.id.save_new_password_button_account_setting_screen);

        // Access user
        this.dataManager.db.collection("Users").document(this.dataManager.getMyId()).get().addOnSuccessListener(documentSnapshot -> {
            User myUser = documentSnapshot.toObject(User.class);

            saveChanges.setOnClickListener(v->{
                assert myUser != null;
                if(!currentPassword.getText().toString().equals(myUser.getPassword())){
                    Toast.makeText(getContext(), "Current password is incorrect", Toast.LENGTH_LONG).show();
                }
                else if (newPassword.getText().toString().equals(""))
                {
                    Toast.makeText(getContext(), "Password cannot be empty!", Toast.LENGTH_LONG).show();
                }

                else if (newPassword.getText().toString().length() != 8)
                {
                    Toast.makeText(getContext(), "Password must contain 8 chars!", Toast.LENGTH_LONG).show();
                }
                else if(!newPassword.getText().toString().equals(repeatPassword.getText().toString()))
                {
                    Toast.makeText(getContext(), "Inconsistent passwords", Toast.LENGTH_LONG).show();
                }
                else{
                    myUser.setPassword(newPassword.getText().toString());
                    dataManager.addToUsers(myUser);
                    dataManager.updateSpForSignIn(myUser.getId(), myUser.getMail(), myUser.getPassword());
                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_LONG).show();
                    Utils.moveBetweenFragments(R.id.the_screen, new settingsFragment(), getActivity(), "settings");
                }
            });
        });
    }
}
