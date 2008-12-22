
package com.google.code.geobeagle;

import android.content.Intent;

public interface IntentFactory {
    Intent createIntent(String actionView, String uri);

    Intent createIntent(String string);
}
