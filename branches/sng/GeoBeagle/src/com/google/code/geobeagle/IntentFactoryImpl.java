
package com.google.code.geobeagle;

import android.content.Intent;

public class IntentFactoryImpl implements IntentFactory {
    private final UriParser mUriParser;

    public IntentFactoryImpl(UriParser uriParser) {
        mUriParser = uriParser;
    }

    public Intent createIntent(String actionView, String uri) {
        return new Intent(actionView, mUriParser.parse(uri));
    }

    public Intent createIntent(String action) {
        return new Intent(action);
    }
}
