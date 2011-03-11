package com.google.code.geobeagle.activity.compass.fieldnotes;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.compass.CompassActivity;

import android.app.Activity;

public class ActivityWithGeocache implements HasGeocache {

    @Override
    public Geocache get(Activity activity) {
        return ((CompassActivity)activity).getGeocache();
    }
}