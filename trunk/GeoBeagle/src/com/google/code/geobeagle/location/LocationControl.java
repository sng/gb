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

package com.google.code.geobeagle.location;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.location.Location;
import android.location.LocationManager;

public class LocationControl {
    private final Provider<LocationManager> mLocationManagerProvider;

    @Inject
    public LocationControl(Provider<LocationManager> locationManagerProvider) {
        mLocationManagerProvider = locationManagerProvider;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.android.geobrowse.GpsControlI#getLocation(android.content.Context)
     */
    public Location getLocation() {
        return mLocationManagerProvider.get().getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
}
