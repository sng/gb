
package com.google.code.geobeagle.data;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.data.GeocacheFactory.Source;

import android.location.Location;

public class GeocacheFromMyLocationFactory {
    private final LocationControl mLocationControl;
    private final GeocacheFactory mGeocacheFactory;

    public GeocacheFromMyLocationFactory(GeocacheFactory geocacheFactory,
            LocationControl locationControl) {
        mGeocacheFactory = geocacheFactory;
        mLocationControl = locationControl;
    }

    public Geocache create() {
        Location location = mLocationControl.getLocation();
        if (location == null) {
            return null;
        }
        long time = location.getTime();
        return mGeocacheFactory.create(String.format("ML%1$tk%1$tM%1$tS", time), String.format(
                "[%1$tk:%1$tM] My Location", time), location.getLatitude(),
                location.getLongitude(), Source.MY_LOCATION, null);
    }
}
