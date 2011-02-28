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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.CacheListCompassListener;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.CacheListViewScrollListener;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
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

import android.app.Activity;
import android.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class GeocacheListPresenterHoneycomb extends GeocacheListPresenter {

    static final int UPDATE_DELAY = 1000;

    @Inject
    public GeocacheListPresenterHoneycomb(CombinedLocationListener combinedLocationListener,
            CombinedLocationManager combinedLocationManager,
            Provider<CacheListCompassListener> cacheListCompassListenerProvider,
            GeocacheListAdapter geocacheListAdapter,
            GeocacheVectors geocacheVectors,
            InflatedGpsStatusWidget inflatedGpsStatusWidget,
            Activity listActivity,
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
                geocacheListAdapter, geocacheVectors, inflatedGpsStatusWidget, listActivity,
                locationControlBuffered, sensorManagerWrapper, updateGpsWidgetRunnable,
                cacheListViewScrollListener, gpsStatusListener, updateFilterWorker,
                filterCleanliness, shakeWaker, updateFilterMediator, searchTarget);
    }

    @Override
    public void onCreateFragment(Object listFragmentA) {
        ListFragment listFragment = (ListFragment)listFragmentA;
        ListView listView = listFragment.getListView();
        NoCachesView noCachesView = (NoCachesView)listView.getEmptyView();
        noCachesView.setSearchTarget(mSearchTarget);
        listView.addHeaderView((View)mInflatedGpsStatusWidget.getTag());
        listFragment.setListAdapter(mGeocacheListAdapter);
        listView.setOnCreateContextMenuListener(new CacheListOnCreateContextMenuListener(
                mGeocacheVectors));
        listView.setOnScrollListener(mScrollListener);
    }
}
