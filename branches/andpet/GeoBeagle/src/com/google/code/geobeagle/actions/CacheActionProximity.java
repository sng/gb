package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.prox.ProximityActivity;

import android.app.Activity;
import android.content.Intent;

public class CacheActionProximity implements CacheAction {
    private Activity mActivity;
    
    public CacheActionProximity(Activity activity) {
        mActivity = activity;
    }
    @Override
    public void act(Geocache cache) {
        final Intent intent = 
            new Intent(mActivity, ProximityActivity.class);
        intent.putExtra("geocacheId", cache.getId());
        mActivity.startActivity(intent);
    }

    @Override
    public String getLabel(Geocache geocache) {
        return mActivity.getResources().getString(R.string.menu_proximity);
    }
}
