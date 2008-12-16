package com.google.code.geobeagle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Destination {
	private final double latitude;
	private final double longitude;
	private String description;

	public Destination(CharSequence location) {
		final String REGEX = "\\s*(\\S+\\s+\\S+)\\s+(\\S+\\s+\\S+)\\s*#?(.*)";
		Matcher m = Pattern.compile(REGEX).matcher(location);
		m.matches();

		latitude = Util.minutesToDegrees(m.group(1));
		longitude = Util.minutesToDegrees(m.group(2));
		description = m.group(3).trim();
	}

	public final double getLatitude() {
		return latitude;
	}

	public final double getLongitude() {
		return longitude;
	}

	public final String getDescription() {
		return description;
	}
}
