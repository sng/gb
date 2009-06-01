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

package com.google.code.geobeagle.cachelistactivity.presenter;

import com.google.code.geobeagle.cachelistactivity.model.GeocacheVector;
import com.google.code.geobeagle.cachelistactivity.model.GeocacheVector.LocationComparator;

import java.util.ArrayList;
import java.util.Collections;

public class DistanceSortStrategy implements SortStrategy {
    private final LocationComparator mLocationComparator;

    public DistanceSortStrategy(LocationComparator locationComparator) {
        mLocationComparator = locationComparator;
    }

    public void sort(ArrayList<GeocacheVector> geocacheVectors) {
        for (GeocacheVector geocacheVector : geocacheVectors) {
            geocacheVector.setDistance(geocacheVector.getDistanceFast());
        }
        Collections.sort(geocacheVectors, mLocationComparator);
    }
}