
package com.google.code.geobeagle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

class OnActivityButtonLinkClickListener implements OnClickListener {
    private final Activity mActivity;

    private final IntentCreator mIntentCreator;

    private LocationSetter mLocationSetter;

    private AlertDialog mDlgError;

    public OnActivityButtonLinkClickListener(Activity activity, AlertDialog dlgError,
            LocationSetter locationSetter, IntentCreator intentCreator) {
        this.mActivity = activity;
        this.mIntentCreator = intentCreator;
        this.mLocationSetter = locationSetter;
        this.mDlgError = dlgError;
    }

    public void onClick(final View v) {
        try {
            // if (previousLocations.contains(gotoFieldContents)) {
            // previousLocations.remove(gotoFieldContents);
            // }
            // previousLocations.add(gotoFieldContents);
            // arrayAdapter.notifyDataSetChanged();
            this.mActivity.startActivity(mIntentCreator.createIntent(new Destination(
                    mLocationSetter.getLocation())));
        } catch (final ActivityNotFoundException e) {
            mDlgError.setMessage("Error: " + e.getMessage()
                    + "\nPlease install the Radar application to use Radar.");
            mDlgError.show();
        } catch (final Exception e) {
            mDlgError.setMessage("Error: " + e.getMessage());
            mDlgError.show();
        }
    }
}
