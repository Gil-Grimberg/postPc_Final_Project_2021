package com.example.centralbark_PostPc_2021;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class LinearLayoutManagerWrapper extends LinearLayoutManager {

    public LinearLayoutManagerWrapper(Context context) {
        super(context);
    }

    public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

}
