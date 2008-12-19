
package com.google.code.geobeagle;

import android.content.Intent;

public class CachePageIntentCreator implements IntentCreator {
    private final UriParser mUriParser;

    public CachePageIntentCreator() {
        mUriParser = new UriParserImpl();
    }

    public CachePageIntentCreator(UriParser uriParser) {
        this.mUriParser = uriParser;
    }

    public Intent createIntent(Destination destination) {
        return mUriParser.createIntent(Intent.ACTION_VIEW, mUriParser.parse(String.format(
                "http://coord.info/%1$s", destination.getDescription())));
    }
}
