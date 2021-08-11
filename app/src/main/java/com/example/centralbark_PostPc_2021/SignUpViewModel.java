package com.example.centralbark_PostPc_2021;

import android.widget.EditText;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignUpViewModel extends ViewModel {

    public MutableLiveData<String> userName = new MutableLiveData<>("");
    public MutableLiveData<String> password = new MutableLiveData<>("");
    public MutableLiveData<String> mail = new MutableLiveData<>("");
    public MutableLiveData<String> birthday = new MutableLiveData<>("");
    public MutableLiveData<String> breed = new MutableLiveData<>("");
    public MutableLiveData<String> city = new MutableLiveData<>("");


}
