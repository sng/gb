package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.prox.ProximityActivity;

import android.app.Activity;
import android.content.Intent;

public class MenuActionProximity implements MenuAction {
    Activity mActivity;

    public MenuActionProximity(Activity activity) {
        mActivity = activity;
    }
    
    @Override
    public void act() {
        final Intent intent = 
            new Intent(mActivity, ProximityActivity.class);
        mActivity.startActivity(intent);
    }

    @Override
    public int getId() {
        return R.string.menu_proximity;
    }

}
