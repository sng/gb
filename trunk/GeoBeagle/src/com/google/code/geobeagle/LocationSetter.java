package com.google.code.geobeagle;

import java.util.ArrayList;

import android.content.Context;

public interface LocationSetter {
	public abstract CharSequence setLocation(double lat, double lon, CharSequence description);

	public abstract CharSequence getLocation();

	public abstract void setLocation(CharSequence c);

	public abstract ArrayList<CharSequence> getPreviousDescriptions();

	public abstract ArrayList<CharSequence> getPreviousLocations();

	public abstract void load(Context c);

	public abstract void save(Context c);
}