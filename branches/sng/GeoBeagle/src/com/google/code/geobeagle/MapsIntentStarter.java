
package com.google.code.geobeagle;

import android.content.Intent;

class MapsIntentStarter implements IntentStarter {
    private final ResourceProvider mResourceProvider;

    public MapsIntentStarter(ResourceProvider resourceProvider) {
        mResourceProvider = resourceProvider;
    }

    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory,
            Destination destination) {
        // "geo:%1$.5f,%2$.5f?name=cachename"
        activityStarter.startActivity(intentFactory.createIntent(Intent.ACTION_VIEW, String.format(
                mResourceProvider.getString(R.string.map_intent), destination.getLatitude(),
                destination.getLongitude(), destination.getDescription())));
    }
}
