package com.example.centralbark_PostPc_2021;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;


public class searchAccountFragment extends Fragment {
    private DataManager dataManager;
    private FirestoreRecyclerAdapter accountsAdapter;
    private RecyclerView accountsRecycler;
    private MenuFragment menuFragment;
    Button searchAccountsButton;
    Button searchPlacesButton;
    EditText searchAccountsEditText;


    public searchAccountFragment(MenuFragment menuFragment) {
        super(R.layout.fragment_search_account);
        this.menuFragment = menuFragment;
        if (dataManager == null){
            dataManager = CentralBarkApp.getInstance().getDataManager();
        }
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.accountsRecycler = (RecyclerView) view.findViewById(R.id.recyclerView_SearchAccounts);
        this.searchAccountsButton = view.findViewById(R.id.searchAccounts_Button);
        this.searchPlacesButton = view.findViewById(R.id.searchPlaces_Button);
        this.searchAccountsEditText = view.findViewById(R.id.searchAccounts_EditText);

        Query query = this.dataManager.db.collection("Users")
                .whereNotIn("id", Collections.singletonList(dataManager.getMyId()));

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();


        this.accountsAdapter = new FirestoreRecyclerAdapter<User, RecyclerAccountsHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public RecyclerAccountsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_search_accounts, parent, false);
                return new RecyclerAccountsHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull RecyclerAccountsHolder holder, int position, @NonNull @NotNull User model) {
                // set the user name
                holder.userName.setText(model.getUsername());

                // set users profile image
                if(!model.getProfilePhoto().equals("default")){
                    StorageReference profileImg = dataManager.storage.getReference().child(model.getProfilePhoto());
                    File localProfileImFile = null;
                    try {
                        localProfileImFile = File.createTempFile("profile_photos", "g");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File profileImFile = localProfileImFile;
                    profileImg.getFile(profileImFile)
                            .addOnSuccessListener(taskSnapshot -> holder.profilePhoto.setImageURI(Uri.fromFile(profileImFile)))
                            .addOnFailureListener(exception -> {
                        // keeps the default profile image
                    });
                }
            else{
                    holder.profilePhoto.setImageResource(R.drawable.default_dog);
                }

                holder.profilePhoto.setOnClickListener(v->{
                    Utils.moveBetweenFragments(R.id.the_screen, new myProfileFragment(model.getId()), getActivity(), "myProfile");
                });

                holder.userName.setOnClickListener(v->{
                    Utils.moveBetweenFragments(R.id.the_screen, new myProfileFragment(model.getId()), getActivity(), "myProfile");
                });
            }
        };

        //edit text was changed
        searchAccountsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() != 0) {
                    Query newQuery = dataManager.db.collection("Users")
                            .orderBy("username").startAt(s.toString()).endAt(s.toString()+"\uf8ff");
                    FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                            .setQuery(newQuery, User.class).build();
                    accountsAdapter.updateOptions(options);
                }
                else{
                    Query query = dataManager.db.collection("Users").whereNotIn("id", Collections.singletonList(dataManager.getMyId()));;
                    FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                            .setQuery(query, User.class).build();
                    accountsAdapter.updateOptions(options);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        this.accountsRecycler.setLayoutManager(new LinearLayoutManagerWrapper(this.getContext(),RecyclerView.VERTICAL,false));
        this.accountsRecycler.setAdapter(accountsAdapter);
        this.searchPlacesButton.setOnClickListener(v->{
            Utils.moveBetweenFragments(R.id.the_screen, new searchPlacesFragment(this.menuFragment), getActivity(), "search_places");
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        this.accountsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.accountsAdapter.stopListening();
    }

    private static class RecyclerAccountsHolder extends RecyclerView.ViewHolder{
        private TextView userName;
        private ImageView profilePhoto;

        @SuppressLint("CutPasteId")
        public RecyclerAccountsHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.userName = itemView.findViewById(R.id.userName_TextView);
            this.profilePhoto = itemView.findViewById(R.id.profilePhoto_ImageView);
        }
    }
}