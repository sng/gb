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

package com.google.code.geobeagle.activity.cachelist.model;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.inject.Inject;

import roboguice.inject.ContextScoped;

import java.util.ArrayList;
@ContextScoped
public class CacheListData {
    private final GeocacheVectors mGeocacheVectors;

    @Inject
    public CacheListData(GeocacheVectors geocacheVectors) {
        mGeocacheVectors = geocacheVectors;
    }

    public void add(ArrayList<Geocache> geocaches, LocationControlBuffered locationControlBuffered) {
        mGeocacheVectors.reset(geocaches.size());
        mGeocacheVectors.addLocations(geocaches, locationControlBuffered);
    }

    public ArrayList<GeocacheVector> get() {
        return mGeocacheVectors.getGeocacheVectorsList();
    }

    public int size() {
        return mGeocacheVectors.size();
    }
}
