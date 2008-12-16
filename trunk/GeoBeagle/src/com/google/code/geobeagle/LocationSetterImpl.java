package com.google.code.geobeagle;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.View.OnFocusChangeListener;

public class LocationSetterImpl implements LocationSetter {
	private static final String FNAME_RECENT_LOCATIONS = "RECENT_LOCATIONS";
	private final DescriptionsAndLocations descriptionsAndLocations;
	private final MockableEditText txtLocation;
	private final GpsControl gpsControl;

	public LocationSetterImpl(Context context, MockableEditText editText, GpsControl gpsControl) {
		txtLocation = editText;
		this.gpsControl = gpsControl;
		descriptionsAndLocations = new DescriptionsAndLocations();
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					saveLocation();
				}
			}
		});
	}

	public CharSequence getLocation() {
		return txtLocation.getText();
	}

	public List<CharSequence> getPreviousDescriptions() {
		return descriptionsAndLocations.getPreviousDescriptions();
	}

	public List<CharSequence> getPreviousLocations() {
		return descriptionsAndLocations.getPreviousLocations();
	}

	public void load(Context c) {
		try {
			descriptionsAndLocations.clear();

			final FileInputStream f = c.openFileInput(FNAME_RECENT_LOCATIONS);
			final InputStreamReader isr = new InputStreamReader(f);
			final BufferedReader br = new BufferedReader(isr);
			CharSequence dataLine = null;

			while ((dataLine = br.readLine()) != null) {
				saveLocation(dataLine);
			}
			
			f.close();
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save(Context c) {
		try {
			final FileOutputStream openFileOutput = c.openFileOutput(FNAME_RECENT_LOCATIONS,
					Context.MODE_PRIVATE);
			final BufferedOutputStream bos = new BufferedOutputStream(openFileOutput);
			for (final CharSequence location : descriptionsAndLocations.getPreviousLocations()) {
				bos.write((location.toString() + "\n").getBytes());
			}
			bos.close();
			openFileOutput.close();
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private CharSequence saveLocation() {
		return saveLocation(getLocation());
	}

	private CharSequence saveLocation(final CharSequence location) {
		final Destination d = new Destination(location);
		final CharSequence description = d.getDescription();
		descriptionsAndLocations.add(description, location);

		return location;
	}

	public void setLocation(CharSequence c) {
		if (c == null) {
			Location location = gpsControl.getLocation();
			setLocation(location.getLatitude(), location.getLongitude(), String.format(
					"[%1$tk:%1$tM] My Location", location.getTime()));
			return;
		}
		saveLocation(c);
		txtLocation.setText(c);
	}

	public CharSequence setLocation(double lat, double lon, CharSequence description) {
		final CharSequence latLonText = Util.degreesToMinutes(lat) + "  "
				+ Util.degreesToMinutes(lon) + " # " + description;
		txtLocation.setText(latLonText);
		saveLocation(latLonText);
		return latLonText;
	}

	public DescriptionsAndLocations getDescriptionsAndLocations() {
		return descriptionsAndLocations;
	}

}
