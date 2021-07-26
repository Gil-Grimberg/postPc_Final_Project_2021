package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class searchAccountFragment extends Fragment {
    DataManager dataManager = CentralBarkApp.getInstance().getDataManager();
    RecyclerView recyclerView;


    public searchAccountFragment() {
        super(R.layout.fragment_search_account);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search_account, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_SearchAccounts);
        adapterSearchAccounts adapter = new adapterSearchAccounts(getContext(), dataManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void addUserToPendingRequestList(User addUser) {
        User myUser = dataManager.getUserById(dataManager.getMyId());
        if (myUser != null){
            myUser.getPendingRequests().add(addUser.getId());
        }
    }

    public ArrayList<User> getUsersList(){
        return dataManager.getAllUsers();
    }
}