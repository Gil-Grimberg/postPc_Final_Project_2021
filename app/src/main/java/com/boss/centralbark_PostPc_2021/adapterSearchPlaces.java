package com.boss.centralbark_PostPc_2021;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.model.LatLng;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Map;

public class adapterSearchPlaces extends RecyclerView.Adapter<adapterSearchPlaces.rowSearchPlacesHolder> {
    Context context;
    Map<LatLng, String> parks;
    ArrayList<LatLng> locations = new ArrayList<>();
    ArrayList<String> parksNames = new ArrayList<>();
    MenuFragment menuFragment;

    public adapterSearchPlaces(Context context,  Map<LatLng, String> parksList, MenuFragment menuFragment){
        this.context = context;
        this.parks = parksList;
        this.menuFragment = menuFragment;
            for (LatLng location: parks.keySet()){
            String name = parks.get(location);
            locations.add(location);
            parksNames.add(name);
        }
    }

    @NonNull
    @NotNull
    @Override
    public rowSearchPlacesHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_search_places, parent, false);
        return new rowSearchPlacesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull rowSearchPlacesHolder holder, int position) {
        holder.dogParkName.setText(parksNames.get(position));
        holder.goNowButton.setOnClickListener(v->{
            Utils.moveBetweenFragments(R.id.the_screen, new MapsFragment(locations.get(position)), (FragmentActivity) context, "maps_fragment");
            Bundle result = new Bundle();
            result.putString("bundleKey", "result");
            menuFragment.getParentFragmentManager().setFragmentResult("requestKey", result);
        });
    }

    @Override
    public int getItemCount() {
        return parks.size();
    }



    public static class rowSearchPlacesHolder extends RecyclerView.ViewHolder{
        private TextView dogParkName;
        private Button goNowButton;

        public rowSearchPlacesHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.dogParkName = itemView.findViewById(R.id.dogParkName_TextView);
            this.goNowButton = itemView.findViewById(R.id.goNow_Button);
        }
    }

}
