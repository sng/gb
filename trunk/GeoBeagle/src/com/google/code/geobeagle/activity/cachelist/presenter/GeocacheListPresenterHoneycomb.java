
package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.CacheListCompassListener;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.CacheListViewScrollListener;
import com.google.code.geobeagle.activity.cachelist.SearchTarget;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.filter.UpdateFilterMediator;
import com.google.code.geobeagle.database.filter.FilterCleanliness;
import com.google.code.geobeagle.database.filter.UpdateFilterWorker;
import com.google.code.geobeagle.gpsstatuswidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.code.geobeagle.shakewaker.ShakeWaker;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.util.Log;

public class GeocacheListPresenterHoneycomb extends GeocacheListPresenter {

    @Inject
    public GeocacheListPresenterHoneycomb(CombinedLocationListener combinedLocationListener,
            CombinedLocationManager combinedLocationManager,
            Provider<CacheListCompassListener> cacheListCompassListenerProvider,
            GeocacheListAdapter geocacheListAdapter,
            GeocacheVectors geocacheVectors,
            InflatedGpsStatusWidget inflatedGpsStatusWidget,
            LocationControlBuffered locationControlBuffered,
            SensorManagerWrapper sensorManagerWrapper,
            UpdateGpsWidgetRunnable updateGpsWidgetRunnable,
            CacheListViewScrollListener cacheListViewScrollListener,
            GpsStatusListener gpsStatusListener,
            UpdateFilterWorker updateFilterWorker,
            FilterCleanliness filterCleanliness,
            ShakeWaker shakeWaker,
            UpdateFilterMediator updateFilterMediator,
            SearchTarget searchTarget) {
        super(combinedLocationListener, combinedLocationManager, cacheListCompassListenerProvider,
                geocacheListAdapter, geocacheVectors, inflatedGpsStatusWidget,
                locationControlBuffered, sensorManagerWrapper, updateGpsWidgetRunnable,
                cacheListViewScrollListener, gpsStatusListener, updateFilterWorker,
                filterCleanliness, shakeWaker, updateFilterMediator, searchTarget);
    }

    @Override
    public void onCreate(ListFragtivity listFragitivity) {
        super.onCreate(listFragitivity);
    }

    public void onCreateFragment(GeocacheListPresenter geocacheListPresenter,
            Object listFragmentObject) {
        Log.d("GeoBeagle", "GeocacheListPresenterHoneycomb::onCreateFragment");
        geocacheListPresenter.onCreate((ListFragtivity)listFragmentObject);
    }
}
