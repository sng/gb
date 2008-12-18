
package com.google.code.geobeagle;

import android.location.Location;
import android.location.LocationProvider;

public class LocationViewerImpl implements LocationViewer {
    private final MockableTextView mCoordinates;
    private final MockableTextView mLastUpdateTime;
    private final MockableTextView mStatus;

    public LocationViewerImpl(MockableTextView coordinates, MockableTextView lastUpdateTime,
            MockableTextView status, Location initialLocation) {
        this.mCoordinates = coordinates;
        this.mLastUpdateTime = lastUpdateTime;
        this.mStatus = status;
        if (initialLocation == null) {
            this.mCoordinates.setText("getting location from gps...");
        } else {
            setLocation(initialLocation);
        }
    }

    public void setLocation(Location location) {
        setLocation(location, location.getTime());
    }

    public void setLocation(Location location, long time) {
        mCoordinates.setText(Util.degreesToMinutes(location.getLatitude()) + " "
                + Util.degreesToMinutes(location.getLongitude()) + "  ±" + location.getAccuracy()
                + "m");
        mLastUpdateTime.setText(Util.formatTime(time));
    }

    public void setStatus(int status) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                mStatus.setText("OUT OF SERVICE");
                break;
            case LocationProvider.AVAILABLE:
                mStatus.setText("AVAILABLE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                mStatus.setText("TEMPORARILY UNAVAILABLE");
                break;
        }
    }
}
