package com.example.centralbark_PostPc_2021;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignInViewModel extends ViewModel {

    public MutableLiveData<String> mail = new MutableLiveData<>("");
    public MutableLiveData<String> password = new MutableLiveData<>("");
    public MutableLiveData<Boolean> rememberMe = new MutableLiveData<>(true);
    public MutableLiveData<Boolean> incorrectLogin = new MutableLiveData<>(false);

}
