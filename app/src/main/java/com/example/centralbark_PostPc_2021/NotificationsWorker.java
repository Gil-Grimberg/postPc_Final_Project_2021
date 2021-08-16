package com.example.centralbark_PostPc_2021;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

public class NotificationsWorker extends ListenableWorker {

    CentralBarkApp appInstance = CentralBarkApp.getInstance();

    public NotificationsWorker(@NonNull Context context, @NonNull WorkerParameters params)
    {
        super(context, params);
    }


    @NonNull
    @NotNull
    @Override
    public ListenableFuture<Result> startWork() {
        // 4 cases:
        // 1. a user is in a dog park
        // 2. a user ask for a friend request
        // 3. a user has a match in tinder
        // 4. a user likes your postt
        return (ListenableFuture<Result>) Result.success();


    }

}
