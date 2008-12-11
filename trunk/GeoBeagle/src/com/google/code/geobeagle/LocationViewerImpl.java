package com.google.code.geobeagle;

import java.util.Calendar;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LocationViewerImpl implements LocationViewer {
	static class LocationViewerOnClickListener implements OnClickListener {
		private final LocationSetter locationSetter;
		private final LocationViewer locationViewer;

		public LocationViewerOnClickListener(LocationViewer locationViewer,
				LocationSetter locationSetter) {
			this.locationSetter = locationSetter;
			this.locationViewer = locationViewer;
		}

		public void onClick(View v) {
			locationSetter.setLocation(locationViewer.getLocation());
		}
	}

	private final Button caption;
	private final TextView coordinates;

	public LocationViewerImpl(final Button caption, TextView coordinates, Location initialLocation) {
		this.coordinates = coordinates;
		this.caption = caption;
		// disabled until coordinates come in.
		caption.setEnabled(false);
		if (initialLocation == null) {
			this.coordinates.setText("getting location from gps...");
		} else {
			setLocation(initialLocation);
		}
	}

	public String getLocation() {
		final String desc = (String) caption.getText();
		return coordinates.getText() + " # " + desc.substring(0, desc.length());
	}

	public void setLocation(Location location) {
		setLocation(location, Calendar.getInstance().getTime().getTime());
	}

	public void setLocation(Location location, long time) {
		caption.setEnabled(true);
		coordinates.setText(Util.degreesToMinutes(location.getLatitude()) + " "
				+ Util.degreesToMinutes(location.getLongitude()));
		caption.setText("GPS@" + Util.formatTime(time));
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		caption.setOnClickListener(onClickListener);
	}

	public void setStatus(int status) {
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			caption.setTextColor(Color.RED);
			break;
		case LocationProvider.AVAILABLE:
			caption.setTextColor(Color.BLACK);
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			caption.setTextColor(Color.DKGRAY);
			break;
		}
	}
}
