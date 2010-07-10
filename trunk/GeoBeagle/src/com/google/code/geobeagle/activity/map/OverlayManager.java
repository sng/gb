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

import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.map.GeoMapActivityModule.NullOverlay;

import android.app.Activity;
import android.util.Log;

import java.util.List;

public class OverlayManager {
    static final int DENSITY_MAP_ZOOM_THRESHOLD = 13;
    private final CachePinsOverlayFactory mCachePinsOverlayFactory;
    private final DensityOverlay mDensityOverlay;
    private final GeoMapView mGeoMapView;
    private final List<Overlay> mMapOverlays;
    private boolean mUsesDensityMap;

    public OverlayManager(Activity activity,
            DensityOverlay densityOverlay, CachePinsOverlayFactory cachePinsOverlayFactory,
            boolean usesDensityMap, MyLocationOverlay myLocationOverlay) {
        final GeoMapView geoMapView = (GeoMapView)activity.findViewById(R.id.mapview);
        mGeoMapView = geoMapView;
        final List<Overlay> mapOverlays = geoMapView.getOverlays();
        mMapOverlays = mapOverlays;
        mDensityOverlay = densityOverlay;
        mCachePinsOverlayFactory = cachePinsOverlayFactory;
        mUsesDensityMap = usesDensityMap;
        final NullOverlay nullOverlay = new NullOverlay();
        mapOverlays.add(nullOverlay);
        mapOverlays.add(myLocationOverlay);
    }

    public void selectOverlay() {
        final int zoomLevel = mGeoMapView.getZoomLevel();
        Log.d("GeoBeagle", "Zoom: " + zoomLevel);
        boolean newZoomUsesDensityMap = zoomLevel < OverlayManager.DENSITY_MAP_ZOOM_THRESHOLD;
        if (newZoomUsesDensityMap && mUsesDensityMap)
            return;
        mUsesDensityMap = newZoomUsesDensityMap;
        mMapOverlays.set(0, mUsesDensityMap ? mDensityOverlay : mCachePinsOverlayFactory
                .getCachePinsOverlay());
    }

    public boolean usesDensityMap() {
        return mUsesDensityMap;
    }
}
