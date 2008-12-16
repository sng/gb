package com.google.code.geobeagle;

import android.content.Intent;
import android.net.Uri;


class MapsIntentCreator implements IntentCreator {
	public Intent createIntent(final Destination latlong) {
//		return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("geo:%1$.5f,%2$.5f?name=hi",
//				latlong.getLatitude(), latlong.getLongitude())));

		return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("geo:0,0?q=%1$.5f,%2$.5f (%3$s)",
				latlong.getLatitude(), latlong.getLongitude(), latlong.getDescription())));
	}
}