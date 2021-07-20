package com.example.centralbark_PostPc_2021;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class adapterSearchAccounts extends RecyclerView.Adapter<adapterSearchAccounts.rowSearchAccountsHolder> {

    @NonNull
    @Override
    public rowSearchAccountsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull rowSearchAccountsHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class rowSearchAccountsHolder extends RecyclerView.ViewHolder{
        public rowSearchAccountsHolder(@NonNull  View itemView) {
            super(itemView);
        }
    }

}
