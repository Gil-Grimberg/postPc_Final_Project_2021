package com.example.centralbark_PostPc_2021;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

public class adapterSearchAccounts extends RecyclerView.Adapter<adapterSearchAccounts.rowSearchAccountsHolder> {

    searchAcountFragment searchAccountHolder;
    Context context;

    public adapterSearchAccounts(Context context, searchAcountFragment fragment){
        this.context = context;
        this.searchAccountHolder = fragment;
    }

    @NonNull
    @Override
    public rowSearchAccountsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_search_accounts, parent, false);
        return new rowSearchAccountsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull rowSearchAccountsHolder holder, int position) {
        User user = searchAccountHolder.getUsersList().get(position);
        // changing user name
        holder.userName.setText(user.getUsername());
        // changing profile photo
        if(!user.getPhoto().equals("default")){
            holder.profilePhoto.setImageURI(Uri.parse(user.getPhoto()));
        }
        // make friend was pressed
        holder.makeFriend.setOnClickListener(v->{
            searchAccountHolder.addUserToPendingRequestList(user);
            holder.makeFriend.setText("pending request");
        });
        //edit text was changed

    }

    @Override
    public int getItemCount() {
        return searchAccountHolder.getUsersList().size();
    }

    public class rowSearchAccountsHolder extends RecyclerView.ViewHolder{
        TextView userName;
        AppCompatButton makeFriend;
        CircularImageView profilePhoto;
        EditText editText;

        @SuppressLint("CutPasteId")
        public rowSearchAccountsHolder(@NonNull  View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName_TextView);
            makeFriend = itemView.findViewById(R.id.makeFriend_AppCompatButton);
            profilePhoto = itemView.findViewById(R.id.makeFriend_AppCompatButton);
            editText = itemView.findViewById(R.id.editText_EditText);
        }
    }

}
