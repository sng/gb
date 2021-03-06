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

package com.google.code.geobeagle.data;


import android.location.Location;

import java.util.ArrayList;

public class CacheListData {
    private final GeocacheVectorFactory mGeocacheVectorFactory;
    private final GeocacheVectors mGeocacheVectors;

    public CacheListData(GeocacheVectors geocacheVectors, GeocacheVectorFactory geocacheVectorFactory) {
        mGeocacheVectors = geocacheVectors;
        mGeocacheVectorFactory = geocacheVectorFactory;
    }

    public void add(ArrayList<Geocache> geocaches, Location here) {
        mGeocacheVectors.reset(geocaches.size());
        mGeocacheVectors.addLocations(geocaches, here);
        mGeocacheVectors.add(mGeocacheVectorFactory.createMyLocation());
        mGeocacheVectors.sort();
    }
}
