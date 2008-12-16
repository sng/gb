package com.google.code.geobeagle;

import android.content.Intent;
import android.net.Uri;

class NearestCachesIntentCreator implements IntentCreator {
	public Intent createIntent(final Destination latlong) {
		return new Intent(Intent.ACTION_VIEW, Uri
				.parse("http://www.geocaching.com/seek/nearest.aspx?"
						+ String.format("lat=%1$.5f&lng=%2$.5f&f=1", latlong
								.getLatitude(), latlong.getLongitude())));
	}
}