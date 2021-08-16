package com.example.centralbark_PostPc_2021;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class adapterSearchPlaces extends RecyclerView.Adapter<adapterSearchPlaces.rowSearchPlacesHolder> {
    searchPlacesFragment searchPlacesFragment = new searchPlacesFragment();
    DataManager dataManager;
    Context context;

    public adapterSearchPlaces(Context context, DataManager dataManager){
        this.context = context;
        this.dataManager = dataManager;
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


    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class rowSearchPlacesHolder extends RecyclerView.ViewHolder{
        private TextView dogPark;
        private Button goNowButton;

        public rowSearchPlacesHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.dogPark = itemView.findViewById(R.id.dogParkName_TextView);
            this.goNowButton = itemView.findViewById(R.id.goNow_Button);
        }
    }

}
