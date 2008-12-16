package com.google.code.geobeagle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

class OnActivityButtonLinkClickListener implements OnClickListener {
	private final Activity activity;
	private final IntentCreator intentCreator;
	private LocationSetter locationSetter;
	private AlertDialog dlgError;

	public OnActivityButtonLinkClickListener(Activity activity, AlertDialog dlgError,
			LocationSetter locationSetter, IntentCreator intentCreator) {
		this.activity = activity;
		this.intentCreator = intentCreator;
		this.locationSetter = locationSetter;
		this.dlgError = dlgError;
	}

	public void onClick(final View v) {
		try {
			// if (previousLocations.contains(gotoFieldContents)) {
			// previousLocations.remove(gotoFieldContents);
			// }
			// previousLocations.add(gotoFieldContents);
			// arrayAdapter.notifyDataSetChanged();
			this.activity.startActivity(intentCreator.createIntent(new Destination(locationSetter
					.getLocation())));
		} catch (final ActivityNotFoundException e) {
			dlgError.setMessage("Error: " + e.getMessage()
					+ "\nPlease install the Radar application to use Radar.");
			dlgError.show();
		} catch (final Exception e) {
			dlgError.setMessage("Error: " + e.getMessage());
			dlgError.show();
		}
	}
}