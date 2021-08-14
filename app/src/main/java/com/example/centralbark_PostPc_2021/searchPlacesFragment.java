package com.example.centralbark_PostPc_2021;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import org.jetbrains.annotations.NotNull;


public class searchPlacesFragment extends Fragment {

    private DataManager dataManager;
    private FirestoreRecyclerAdapter placesAdapter;
    private RecyclerView placesRecycler;
    Button searchAccountsButton;
    Button searchPlacesButton;
    EditText searchPlacesEditText;

    public searchPlacesFragment() {
        super(R.layout.fragment_search_places);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search_places, container, false);
        placesRecycler = view.findViewById(R.id.recyclerView_SearchPlaces);
        adapterSearchPlaces adapter = new adapterSearchPlaces(getContext(), dataManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        placesRecycler.setLayoutManager(linearLayoutManager);
        placesRecycler.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        this.searchPlacesButton.setOnClickListener(v->{
//            Utils.moveBetweenFragments(R.id.the_screen, new searchPlacesFragment(), getActivity(), "search_places");
    }
}