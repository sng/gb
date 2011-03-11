package com.google.code.geobeagle.activity.compass.fieldnotes;

import com.google.code.geobeagle.Geocache;

import android.app.Activity;

public interface HasGeocache {
    Geocache get(Activity activity);
}