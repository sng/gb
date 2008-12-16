
package com.google.code.geobeagle;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationProvider;
import android.view.View;
import android.view.View.OnClickListener;

public class LocationViewerImpl implements LocationViewer {
    static class LocationViewerOnClickListener implements OnClickListener {
        private final LocationSetter mLocationSetter;

        private final LocationViewer mLocationViewer;

        public LocationViewerOnClickListener(LocationViewer locationViewer,
                LocationSetter locationSetter) {
            this.mLocationSetter = locationSetter;
            this.mLocationViewer = locationViewer;
        }

        public void onClick(View v) {
            mLocationSetter.setLocation(mLocationViewer.getLocation());
        }
    }

    private final MockableButton mCaption;

    private final MockableTextView mCoordinates;

    public LocationViewerImpl(final MockableButton button, MockableTextView coordinates,
            Location initialLocation) {
        this.mCoordinates = coordinates;
        this.mCaption = button;
        // disabled until coordinates come in.
        button.setEnabled(false);
        if (initialLocation == null) {
            this.mCoordinates.setText("getting location from gps...");
        } else {
            setLocation(initialLocation);
        }
    }

    public String getLocation() {
        final String desc = (String)mCaption.getText();
        return mCoordinates.getText() + " # " + desc.substring(0, desc.length());
    }

    public void setLocation(Location location) {
        setLocation(location, location.getTime());
    }

    public void setLocation(Location location, long time) {
        mCaption.setEnabled(true);
        mCoordinates.setText(Util.degreesToMinutes(location.getLatitude()) + " "
                + Util.degreesToMinutes(location.getLongitude()));
        mCaption.setText("GPS@" + Util.formatTime(time));
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mCaption.setOnClickListener(onClickListener);
    }

    public void setStatus(int status) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                mCaption.setTextColor(Color.RED);
                break;
            case LocationProvider.AVAILABLE:
                mCaption.setTextColor(Color.BLACK);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                mCaption.setTextColor(Color.DKGRAY);
                break;
        }
    }
}
