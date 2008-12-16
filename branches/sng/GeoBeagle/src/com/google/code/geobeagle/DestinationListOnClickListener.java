package com.google.code.geobeagle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

public class DestinationListOnClickListener implements OnClickListener {
	public static final String MY_LOCATION = "My Location";

	public static class DestinationListDialogOnClickListener
			implements
				DialogInterface.OnClickListener {
		private final LocationSetter locationSetter;
		private final List<CharSequence> previousLocations;

		public DestinationListDialogOnClickListener(List<CharSequence> previousLocations,
				LocationSetter locationSetter) {
			this.previousLocations = previousLocations;
			this.locationSetter = locationSetter;
		}

		public void onClick(DialogInterface dialog, int which) {
			locationSetter.setLocation(previousLocations.get(which));
		}
	}

	final private Builder dialogBuilder;
	final private LocationSetter locationSetter;
	private DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();

	public DestinationListOnClickListener(DescriptionsAndLocations descriptionsAndLocations,
			LocationSetter locationSetter, AlertDialog.Builder dialogBuilder) {
		this.dialogBuilder = dialogBuilder;
		this.locationSetter = locationSetter;
		this.descriptionsAndLocations = descriptionsAndLocations;
	}

	protected DialogInterface.OnClickListener createDestinationListDialogOnClickListener(
			List<CharSequence> previousLocations) {
		return new DestinationListDialogOnClickListener(previousLocations, locationSetter);
	}

	public void onClick(View v) {
		showDestinationListDialog();
	}

	private void showDestinationListDialog() {
		// Display "My Location" followed by descriptions in reverse order (most
		// recent descriptions first).
		List<CharSequence> dialogPreviousDescriptions = getDisplayableList(
				descriptionsAndLocations.getPreviousDescriptions(),
				DestinationListOnClickListener.MY_LOCATION);
		List<CharSequence> dialogPreviousLocations = getDisplayableList(
				descriptionsAndLocations.getPreviousLocations(), null);

		final CharSequence[] prevDescriptionsArray = dialogPreviousDescriptions
				.toArray(new CharSequence[dialogPreviousDescriptions.size()]);
		dialogBuilder.setTitle(R.string.select_destination);
		dialogBuilder.setItems(prevDescriptionsArray,
				createDestinationListDialogOnClickListener(dialogPreviousLocations)).create()
				.show();
	}

	private List<CharSequence> getDisplayableList(List<CharSequence> list, String myLocation) {
		List<CharSequence> dialogPreviousDescriptions = new ArrayList<CharSequence>();
		dialogPreviousDescriptions.addAll(list);
		dialogPreviousDescriptions.add(myLocation);
		Collections.reverse(dialogPreviousDescriptions);
		return dialogPreviousDescriptions;
	}
}