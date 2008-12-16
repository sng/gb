
package com.google.code.geobeagle;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public final class GpsLocationListener implements LocationListener {
    // private AlertDialog dlgGpsDisabled;
    private LocationViewer mLocationViewer;

    public GpsLocationListener(LocationViewer locationViewer, Context context) {
        this.mLocationViewer = locationViewer;
        // Builder builder = new AlertDialog.Builder(context);
        // builder.setIcon(android.R.drawable.ic_dialog_alert);
        // builder.setTitle("GPS DISABLED!");
        // builder.setMessage("Please enable it in Settings / Security & Location.");
        // dlgGpsDisabled = builder.create();
    }

    public void onLocationChanged(Location location) {
        mLocationViewer.setLocation(location);
    }

    public void onProviderDisabled(String provider) {
        // dlgGpsDisabled.show();
    }

    public void onProviderEnabled(String provider) {
        // dlgGpsDisabled.hide();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        mLocationViewer.setStatus(status);
    }
}
