package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.activity.cachelist.CacheListActivityHoneycomb;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.Intent;

public class MenuActionCacheListHoneycomb implements Action {
    private final Activity mActivity;

    @Inject
    public MenuActionCacheListHoneycomb(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void act() {
        mActivity.startActivity(new Intent(mActivity, CacheListActivityHoneycomb.class));
    }
}
