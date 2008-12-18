
package com.google.code.geobeagle;

import android.content.Intent;

public class Radar {
    static class RadarIntentCreator implements IntentCreator {
        public Intent createIntent(final Destination destination) {
            final Intent i = new Intent("com.google.android.radar.SHOW_RADAR");
            i.putExtra("latitude", (float)destination.getLatitude());
            i.putExtra("longitude", (float)destination.getLongitude());
            return i;
        }
    }
}
