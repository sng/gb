
package com.google.code.geobeagle;

import android.content.Intent;

class GeocachingMapsIntentStarter implements IntentStarter {
    private final GetCoordsToast mGetCoordsToast;
    private final ResourceProvider mResourceProvider;

    public GeocachingMapsIntentStarter(GetCoordsToast getCoordsToast, ResourceProvider resourceProvider) {
        mGetCoordsToast = getCoordsToast;
        mResourceProvider = resourceProvider;
    }

    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory,
            Destination destination) {
        mGetCoordsToast.show();
        activityStarter.startActivity(intentFactory.createIntent(Intent.ACTION_VIEW, String.format(
                mResourceProvider.getString(R.string.geocaching_maps_url), destination
                        .getLatitude(), destination.getLongitude())));
    }
}
