
package com.google.code.geobeagle;

import android.location.Location;
import android.location.LocationProvider;

public class LocationViewerImpl implements LocationViewer {
    private final MockableTextView mCoordinates;
    private final MockableTextView mLastUpdateTime;
    private final MockableTextView mStatus;
    private final MockableContext mContext;

    public LocationViewerImpl(MockableContext context, MockableTextView coordinates, MockableTextView lastUpdateTime,
            MockableTextView status, Location initialLocation) {
        this.mContext = context;
        this.mCoordinates = coordinates;
        this.mLastUpdateTime = lastUpdateTime;
        this.mStatus = status;
        if (initialLocation == null) {
            this.mCoordinates.setText(R.string.getting_location_from_gps);
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
                mStatus.setText(mContext.getString(R.string.out_of_service));
                break;
            case LocationProvider.AVAILABLE:
                mStatus.setText(mContext.getString(R.string.available));
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                mStatus.setText(mContext.getString(R.string.temporarily_unavailable));
                break;
        }
    }
}
