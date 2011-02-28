package com.google.code.geobeagle.activity.cachelist;

import android.app.Activity;

class FragmentBuilderHoneycomb {
    public void onCreate(Activity activity) {
        CacheListFragment cacheListFragment = new CacheListFragment();
        cacheListFragment.setArguments(activity.getIntent().getExtras());
        activity.getFragmentManager().beginTransaction()
                .add(android.R.id.content, cacheListFragment).commit();
    }
}