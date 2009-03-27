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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.Util;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheFromPreferencesFactory;

import android.widget.TextView;

public class GeocacheViewer {

    public static final String FNAME_RECENT_LOCATIONS = "RECENT_LOCATIONS";
    public static final String PREFS_LOCATION = "Location";
    private final TextView mTxtLocation;

    public GeocacheViewer(TextView mockableTxtLocation,
            GeocacheFromPreferencesFactory geocacheFactory) {
        mTxtLocation = mockableTxtLocation;
    }

    public void set(Geocache geocache) {
        final CharSequence latLonText = Util.formatDegreesAsDecimalDegreesString(geocache
                .getLatitude())
                + ", "
                + Util.formatDegreesAsDecimalDegreesString(geocache.getLongitude())
                + " ("
                + geocache.getIdAndName() + ")";
        mTxtLocation.setText(latLonText);
    }
}
