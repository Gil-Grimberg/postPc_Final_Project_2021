package com.boss.centralbark_PostPc_2021;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.maps.model.LatLng;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;


public class searchPlacesFragment extends Fragment {

    private RecyclerView placesRecycler;
    Button searchAccountsButton;
    EditText searchPlacesEditText;
    adapterSearchPlaces adapter;
    MenuFragment menuFragment;

    public searchPlacesFragment(MenuFragment menuFragment) {
        super(R.layout.fragment_search_places);
        this.menuFragment = menuFragment;
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchAccountsButton = view.findViewById(R.id.searchAccounts_Button_searchPlaces);
        searchPlacesEditText = view.findViewById(R.id.searchPlaces_EditText);
        placesRecycler = view.findViewById(R.id.recyclerView_SearchPlaces);

        adapter = new adapterSearchPlaces(getContext(), Utils.locationToNameMapping,this.menuFragment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManagerWrapper(getContext());
        placesRecycler.setLayoutManager(linearLayoutManager);
        placesRecycler.setAdapter(adapter);

        searchAccountsButton.setOnClickListener(v -> {
            Utils.moveBetweenFragments(R.id.the_screen, new searchAccountFragment(this.menuFragment), getActivity(), "search_accounts");
        });

        searchPlacesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() != 0) {
                    Map<LatLng, String> filteredMap = new HashMap<>();
                    Map<LatLng, String> parks = Utils.locationToNameMapping;
                    for (LatLng location : parks.keySet()) {
                        String parkName = parks.get(location);
                        assert parkName != null;
                        if (parkName.toLowerCase().startsWith(s.toString().toLowerCase())) {
                            filteredMap.put(location, parkName);
                        }
                    }
                    adapter = new adapterSearchPlaces(getContext(), filteredMap,menuFragment);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManagerWrapper(getContext());
                    placesRecycler.setLayoutManager(linearLayoutManager);
                }
                else{
                    adapter = new adapterSearchPlaces(getContext(), Utils.locationToNameMapping,menuFragment);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManagerWrapper(getContext());
                    placesRecycler.setLayoutManager(linearLayoutManager);
                }
                placesRecycler.setAdapter(adapter);
            }
        });
    }
}