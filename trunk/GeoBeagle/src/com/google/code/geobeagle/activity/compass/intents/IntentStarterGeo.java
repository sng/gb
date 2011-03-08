/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.activity.compass.intents;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.CompassFragment;

import android.app.Activity;
import android.content.Intent;

public class IntentStarterGeo implements IntentStarter {
    private final Activity mGeoBeagle;
    private final Intent mIntent;

    public IntentStarterGeo(Activity geoBeagle, Intent intent) {
        mGeoBeagle = geoBeagle;
        mIntent = intent;
    }

    @Override
    public void startIntent() {
        CompassFragment fragment = (CompassFragment)mGeoBeagle.getFragmentManager().findFragmentById(R.id.compass_fragment);
        Geocache geocache = fragment.getGeocache();
        mIntent.putExtra("latitude", (float)geocache.getLatitude());
        mIntent.putExtra("longitude", (float)geocache.getLongitude());
        mGeoBeagle.startActivity(mIntent);
    }
}
