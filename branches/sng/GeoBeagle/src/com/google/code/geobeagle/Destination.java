
package com.google.code.geobeagle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Destination {
    private final double mLatitude;

    private final double mLongitude;

    private String mDescription;

    public Destination(CharSequence location) {
        final String REGEX = "\\s*(\\S+\\s+\\S+)\\s+(\\S+\\s+\\S+)\\s*#?(.*)";
        Matcher m = Pattern.compile(REGEX).matcher(location);
        m.matches();

        mLatitude = Util.minutesToDegrees(m.group(1));
        mLongitude = Util.minutesToDegrees(m.group(2));
        mDescription = m.group(3).trim();
    }

    public final double getLatitude() {
        return mLatitude;
    }

    public final double getLongitude() {
        return mLongitude;
    }

    public final String getDescription() {
        return mDescription;
    }
}
