
package com.google.code.geobeagle;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

class OnActivityButtonLinkClickListener implements OnClickListener {
    private final ActivityStarter mActivityStarter;
    private final IntentStarter mIntentStarter;
    private final LocationSetter mLocationSetter;
    private final AlertDialog mDlgError;
    private final IntentFactory mIntentFactory;

    public OnActivityButtonLinkClickListener(IntentFactory intentFactory, IntentStarter intentStarter,
            ActivityStarter activityStarter, LocationSetter locationSetter,
            AlertDialog dlgError) {
        this.mIntentFactory = intentFactory;
        this.mIntentStarter = intentStarter;
        this.mActivityStarter = activityStarter;
        this.mLocationSetter = locationSetter;
        this.mDlgError = dlgError;
    }

    public void onClick(final View view) {
        try {
            mIntentStarter.startIntent(mActivityStarter, mIntentFactory, new Destination(mLocationSetter
                    .getLocation()));
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
