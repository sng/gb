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
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.activity.map.DensityMatrix.DensityPatch;
import com.google.code.geobeagle.activity.map.QueryManager.CachedNeedsLoading;
import com.google.code.geobeagle.activity.map.QueryManager.LoaderImpl;
import com.google.code.geobeagle.activity.map.QueryManager.PeggedLoader;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GeoMapActivity extends MapActivity {

    private static class NullOverlay extends Overlay {
    }

    private static final int DEFAULT_ZOOM_LEVEL = 14;

    private static boolean fZoomed = false;
    private DbFrontend mDbFrontend;
    private GeoMapActivityDelegate mGeoMapActivityDelegate;
    private GeoMapView mMapView;
    private MyLocationOverlay mMyLocationOverlay;

    @Override
    protected boolean isRouteDisplayed() {
        // This application doesn't use routes
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // Set member variables first, in case anyone after this needs them.
        mMapView = (GeoMapView)findViewById(R.id.mapview);
        mDbFrontend = new DbFrontend(this);
        // mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMyLocationOverlay = new FixedMyLocationOverlay(this, mMapView);

        mMapView.setBuiltInZoomControls(true);
        mMapView.setSatellite(false);

        final Resources resources = getResources();
        final Drawable defaultMarker = resources.getDrawable(R.drawable.map_pin2_others);
        final CacheDrawables cacheDrawables = new CacheDrawables(resources);
        final CacheItemFactory cacheItemFactory = new CacheItemFactory(cacheDrawables);

        final List<Overlay> mapOverlays = mMapView.getOverlays();
        final MenuActions menuActions = new MenuActions(getResources());
        menuActions.add(new GeoMapActivityDelegate.MenuActionToggleSatellite(mMapView));
        menuActions.add(new MenuActionCacheList(this));
        final MapController mapController = mMapView.getController();
        menuActions.add(new GeoMapActivityDelegate.MenuActionCenterLocation(mapController,
                mMyLocationOverlay));

        final Intent intent = getIntent();
        final double latitude = intent.getFloatExtra("latitude", 0);
        final double longitude = intent.getFloatExtra("longitude", 0);
        final Overlay nullOverlay = new GeoMapActivity.NullOverlay();
        final GeoPoint nullGeoPoint = new GeoPoint(0, 0);

        mapOverlays.add(nullOverlay);
        mapOverlays.add(mMyLocationOverlay);

        final ArrayList<Geocache> nullList = new ArrayList<Geocache>();
        final List<DensityPatch> densityPatches = new ArrayList<DensityPatch>();
        final Toaster toaster = new Toaster(this, R.string.too_many_caches, Toast.LENGTH_SHORT);
        final LoaderImpl loaderImpl = new QueryManager.LoaderImpl(mDbFrontend);
        final PeggedLoader peggedLoader = new QueryManager.PeggedLoader(mDbFrontend, nullList,
                toaster, loaderImpl);
        final int[] densityMapInitialLatLonMinMax = {
                0, 0, 0, 0
        };

        final CachedNeedsLoading densityMapCachedNeedsLoading = new CachedNeedsLoading(
                nullGeoPoint, nullGeoPoint);
        final QueryManager densityMapQueryManager = new QueryManager(peggedLoader,
                densityMapCachedNeedsLoading, densityMapInitialLatLonMinMax);
        final DensityOverlayDelegate densityOverlayDelegate = DensityOverlay.createDelegate(
                densityPatches, nullGeoPoint, densityMapQueryManager);
        final DensityOverlay densityOverlay = new DensityOverlay(densityOverlayDelegate);
        final ArrayList<Geocache> geocacheList = new ArrayList<Geocache>();
        final CachePinsOverlay cachePinsOverlay = new CachePinsOverlay(cacheItemFactory, this,
                defaultMarker, geocacheList);
        CachedNeedsLoading cachePinsCachedNeedsLoading = new CachedNeedsLoading(nullGeoPoint,
                nullGeoPoint);
        final int[] cachePinsInitialLatLonMinMax = {
                0, 0, 0, 0
        };
        final QueryManager cachePinsQueryManager = new QueryManager(loaderImpl,
                cachePinsCachedNeedsLoading, cachePinsInitialLatLonMinMax);
        final CachePinsOverlayFactory cachePinsOverlayFactory = new CachePinsOverlayFactory(
                mMapView, this, defaultMarker, cacheItemFactory, cachePinsOverlay,
                cachePinsQueryManager);
        mGeoMapActivityDelegate = new GeoMapActivityDelegate(mMapView, menuActions);

        final GeoPoint center = new GeoPoint((int)(latitude * GeoUtils.MILLION),
                (int)(longitude * GeoUtils.MILLION));

        mapController.setCenter(center);
        final OverlayManager overlayManager = new OverlayManager(mMapView, mapOverlays,
                densityOverlay, cachePinsOverlayFactory, false);
        mMapView.setScrollListener(overlayManager);

        if (!fZoomed) {
            mapController.setZoom(DEFAULT_ZOOM_LEVEL);
            fZoomed = true;
        }

        overlayManager.selectOverlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mGeoMapActivityDelegate.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return mGeoMapActivityDelegate.onMenuOpened(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mGeoMapActivityDelegate.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        mMyLocationOverlay.disableMyLocation();
        mMyLocationOverlay.disableCompass();
        mDbFrontend.closeDatabase();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableCompass();
        // Is this necessary? Or should we remove it and make openDatabase
        // private?
        mDbFrontend.openDatabase();
    }
}
