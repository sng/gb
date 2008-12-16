package com.google.code.geobeagle;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class GpsControlImpl implements GpsControl {
	
	private Context context;

	public GpsControlImpl(Context context) {
		this.context = context;
	}
	/* (non-Javadoc)
	 * @see com.android.geobrowse.GpsControlI#getLocation(android.content.Context)
	 */
	public Location getLocation() {
		return getLocationManagerFromContext(context).getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	private static LocationManager getLocationManagerFromContext(Context context) {
		return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	public static void onPause(Context context, LocationListener locationListener) {
		getLocationManagerFromContext(context).removeUpdates(locationListener);
	}

	public static void onResume(Context context, LocationListener locationListener) {
		getLocationManagerFromContext(context).requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, locationListener);
	}

}
