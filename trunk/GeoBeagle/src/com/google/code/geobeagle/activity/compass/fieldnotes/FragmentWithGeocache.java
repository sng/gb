package com.google.code.geobeagle.activity.compass.fieldnotes;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.CompassFragment;

import android.app.Activity;

public class FragmentWithGeocache implements HasGeocache {

    @Override
    public Geocache get(Activity activity) {
        CompassFragment fragment = (CompassFragment)activity.getFragmentManager()
                .findFragmentById(R.id.compass_frame);
        return fragment.getGeocache();
    }
}