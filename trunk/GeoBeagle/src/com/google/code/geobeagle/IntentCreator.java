
package com.google.code.geobeagle;

import android.content.Intent;

public interface IntentCreator {
    public Intent createIntent(Destination latlong);
}
