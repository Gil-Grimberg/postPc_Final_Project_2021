package com.example.centralbark_PostPc_2021;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.RecyclerPosts>{

    static class RecyclerPosts extends RecyclerView.ViewHolder{
        private ImageView profileIm;
        private TextView userName;
        private ImageView postIm;
        private Button likeButton;
        private TextView numOfLikes;
        private TextView postTime;
        public RecyclerPosts(View view){
            super(view);
            this.profileIm = view.findViewById(R.id.profile_image_one_post_screen);
            this.userName = view.findViewById(R.id.user_name_textview_one_post_screen);
            this.postIm = view.findViewById(R.id.post_image_one_post_screen);
            this.likeButton = view.findViewById(R.id.like_button_one_post_screen);
            this.numOfLikes = view.findViewById(R.id.num_of_likes_textview_one_post_screen);
            this.postTime = view.findViewById(R.id.post_time_textview_one_post_screen);
        }
    }

    private DataManager dataManager;
    private Context context;
    private FeedFragment feedFragment;

    public AdapterPosts(Context context, FeedFragment feedFragment){
        this.context = context;
        this.dataManager = CentralBarkApp.getInstance().getDataManager();
        this.feedFragment = feedFragment;
    }
    @NotNull
    @Override
    public RecyclerPosts onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerPosts holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

}
