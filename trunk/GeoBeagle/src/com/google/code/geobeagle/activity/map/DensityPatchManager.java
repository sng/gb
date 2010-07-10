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

package com.google.code.geobeagle.activity.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.map.DensityMatrix.DensityPatch;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

class DensityPatchManager {
    private List<DensityMatrix.DensityPatch> mDensityPatches;
    private final QueryManager mQueryManager;
    public static final double RESOLUTION_LATITUDE = 0.01;
    public static final double RESOLUTION_LONGITUDE = 0.02;
    public static final int RESOLUTION_LATITUDE_E6 = (int)(RESOLUTION_LATITUDE * 1E6);
    public static final int RESOLUTION_LONGITUDE_E6 = (int)(RESOLUTION_LONGITUDE * 1E6);

    @Inject
    DensityPatchManager(QueryManager queryManager) {
        mDensityPatches = new ArrayList<DensityPatch>();
        mQueryManager = queryManager;
    }

    public List<DensityMatrix.DensityPatch> getDensityPatches(MapView mMapView) {
        Projection projection = mMapView.getProjection();
        GeoPoint newTopLeft = projection.fromPixels(0, 0);
        GeoPoint newBottomRight = projection.fromPixels(mMapView.getRight(), mMapView.getBottom());

        if (!mQueryManager.needsLoading(newTopLeft, newBottomRight)) {
            return mDensityPatches;
        }

        ArrayList<Geocache> list = mQueryManager.load(newTopLeft, newBottomRight);
        DensityMatrix densityMatrix = new DensityMatrix(DensityPatchManager.RESOLUTION_LATITUDE,
                DensityPatchManager.RESOLUTION_LONGITUDE);
        densityMatrix.addCaches(list);
        mDensityPatches = densityMatrix.getDensityPatches();
        // Log.d("GeoBeagle", "Density patches:" + mDensityPatches.size());
        return mDensityPatches;
    }
}
