
package com.google.code.geobeagle;

import android.content.Intent;

public class CachePageIntentStarter implements IntentStarter {
    private final ResourceProvider mResourceProvider;

    public CachePageIntentStarter(ResourceProvider resourceProvider) {
        mResourceProvider = resourceProvider;
    }

    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory,
            Destination destination) {
        activityStarter.startActivity(intentFactory
                .createIntent(Intent.ACTION_VIEW, String.format(mResourceProvider
                        .getString(R.string.cache_page_url), destination.getDescription())));
    }
}
