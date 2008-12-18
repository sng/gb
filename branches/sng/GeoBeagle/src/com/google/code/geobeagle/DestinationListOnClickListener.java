
package com.google.code.geobeagle;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DestinationListOnClickListener implements OnClickListener {
    public static class DestinationListDialogOnClickListener implements
            DialogInterface.OnClickListener {
        private final LocationSetter mLocationSetter;
        private final List<CharSequence> mPreviousLocations;
        private final ErrorDisplayer mErrorDisplayer;

        public DestinationListDialogOnClickListener(List<CharSequence> previousLocations,
                LocationSetter locationSetter, ErrorDisplayer errorDisplayer) {
            this.mPreviousLocations = previousLocations;
            this.mLocationSetter = locationSetter;
            this.mErrorDisplayer = errorDisplayer;
        }

        public void onClick(DialogInterface dialog, int which) {
            mLocationSetter.setLocation(mPreviousLocations.get(which), mErrorDisplayer);
        }
    }

    public static final String MY_LOCATION = "My Current Location";
    final private DescriptionsAndLocations mDescriptionsAndLocations;
    final private Builder mDialogBuilder;
    final private LocationSetter mLocationSetter;
    private ErrorDisplayer mErrorDisplayer;

    public DestinationListOnClickListener(DescriptionsAndLocations descriptionsAndLocations,
            LocationSetter locationSetter, AlertDialog.Builder dialogBuilder,
            ErrorDisplayer errorDisplayer) {
        this.mDialogBuilder = dialogBuilder;
        this.mLocationSetter = locationSetter;
        this.mDescriptionsAndLocations = descriptionsAndLocations;
        this.mErrorDisplayer = errorDisplayer;
    }

    protected DialogInterface.OnClickListener createDestinationListDialogOnClickListener(
            List<CharSequence> previousLocations) {
        return new DestinationListDialogOnClickListener(previousLocations, mLocationSetter,
                mErrorDisplayer);
    }

    private List<CharSequence> getDisplayableList(List<CharSequence> list, String myLocation) {
        List<CharSequence> dialogPreviousDescriptions = new ArrayList<CharSequence>();
        dialogPreviousDescriptions.addAll(list);
        dialogPreviousDescriptions.add(myLocation);
        Collections.reverse(dialogPreviousDescriptions);
        return dialogPreviousDescriptions;
    }

    public void onClick(View v) {
        showDestinationListDialog();
    }

    private void showDestinationListDialog() {
        // Display "My Location" followed by descriptions in reverse order (most
        // recent descriptions first).
        List<CharSequence> dialogPreviousDescriptions = getDisplayableList(
                mDescriptionsAndLocations.getPreviousDescriptions(),
                DestinationListOnClickListener.MY_LOCATION);
        List<CharSequence> dialogPreviousLocations = getDisplayableList(mDescriptionsAndLocations
                .getPreviousLocations(), null);
        final CharSequence[] prevDescriptionsArray = dialogPreviousDescriptions
                .toArray(new CharSequence[dialogPreviousDescriptions.size()]);
        mDialogBuilder.setTitle(R.string.select_destination);
        mDialogBuilder.setItems(prevDescriptionsArray,
                createDestinationListDialogOnClickListener(dialogPreviousLocations)).create()
                .show();
    }
}
