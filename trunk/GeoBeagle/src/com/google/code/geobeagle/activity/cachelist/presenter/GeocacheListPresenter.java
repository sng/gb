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
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.CacheListView;
import com.google.code.geobeagle.activity.cachelist.CacheListView.ScrollListener;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.Pausable;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.database.ClearFilterProgressDialog;
import com.google.code.geobeagle.database.FilterCleanliness;
import com.google.code.geobeagle.database.UpdateFilterWorker;
import com.google.code.geobeagle.gpsstatuswidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.code.geobeagle.shakewaker.ShakeWaker;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class GeocacheListPresenter implements Pausable {

    static final int UPDATE_DELAY = 1000;

    private final LocationListener mCombinedLocationListener;
    private final CombinedLocationManager mCombinedLocationManager;
    private final Provider<CacheListCompassListener> mCacheListCompassListenerProvider;
    private final GeocacheListAdapter mGeocacheListAdapter;
    private final GeocacheVectors mGeocacheVectors;
    private final InflatedGpsStatusWidget mInflatedGpsStatusWidget;
    private final ListActivity mListActivity;
    private final LocationControlBuffered mLocationControlBuffered;
    private final SensorManagerWrapper mSensorManagerWrapper;
    private final UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;
    private final CacheListView.ScrollListener mScrollListener;
    private final GpsStatusListener mGpsStatusListener;
    private final UpdateFilterWorker mUpdateFilterWorker;
    private final UpdateFlag mUpdateFlag;
    private final Provider<ClearFilterProgressDialog> mProgressDialogProvider;
    private final FilterCleanliness mFilterCleanliness;
    private final ShakeWaker mShakeWaker;

    public GeocacheListPresenter(CombinedLocationListener combinedLocationListener,
            CombinedLocationManager combinedLocationManager,
            Provider<CacheListCompassListener> cacheListCompassListenerProvider,
            GeocacheListAdapter geocacheListAdapter,
            GeocacheVectors geocacheVectors,
            InflatedGpsStatusWidget inflatedGpsStatusWidget,
            Activity listActivity,
            LocationControlBuffered locationControlBuffered,
            SensorManagerWrapper sensorManagerWrapper,
            UpdateGpsWidgetRunnable updateGpsWidgetRunnable,
            ScrollListener scrollListener,
            GpsStatusListener gpsStatusListener,
            UpdateFilterWorker updateFilterWorker,
            UpdateFlag updateFlag,
            Provider<ClearFilterProgressDialog> progressDialogProvider,
            FilterCleanliness filterCleanliness,
            ShakeWaker shakeWaker) {
        mCombinedLocationListener = combinedLocationListener;
        mCombinedLocationManager = combinedLocationManager;
        mCacheListCompassListenerProvider = cacheListCompassListenerProvider;
        mGeocacheListAdapter = geocacheListAdapter;
        mGeocacheVectors = geocacheVectors;
        mInflatedGpsStatusWidget = inflatedGpsStatusWidget;
        mShakeWaker = shakeWaker;
        mListActivity = (ListActivity)listActivity;
        mLocationControlBuffered = locationControlBuffered;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mSensorManagerWrapper = sensorManagerWrapper;
        mScrollListener = scrollListener;
        mGpsStatusListener = gpsStatusListener;
        mUpdateFilterWorker = updateFilterWorker;
        mUpdateFlag = updateFlag;
        mProgressDialogProvider = progressDialogProvider;
        mFilterCleanliness = filterCleanliness;
    }

    @Inject
    public GeocacheListPresenter(Injector injector) {
        mCombinedLocationListener = injector.getInstance(CombinedLocationListener.class);
        mCombinedLocationManager = injector.getInstance(CombinedLocationManager.class);
        mCacheListCompassListenerProvider = injector.getProvider(CacheListCompassListener.class);
        mGeocacheListAdapter = injector.getInstance(GeocacheListAdapter.class);
        mGeocacheVectors = injector.getInstance(GeocacheVectors.class);
        mInflatedGpsStatusWidget = injector.getInstance(InflatedGpsStatusWidget.class);
        mShakeWaker = injector.getInstance(ShakeWaker.class);
        mListActivity = (ListActivity)injector.getInstance(Activity.class);
        mLocationControlBuffered = injector.getInstance(LocationControlBuffered.class);
        mUpdateGpsWidgetRunnable = injector.getInstance(UpdateGpsWidgetRunnable.class);
        mSensorManagerWrapper = injector.getInstance(SensorManagerWrapper.class);
        mScrollListener = injector.getInstance(ScrollListener.class);
        mGpsStatusListener = injector.getInstance(GpsStatusListener.class);
        mUpdateFilterWorker = injector.getInstance(UpdateFilterWorker.class);
        mUpdateFlag = injector.getInstance(UpdateFlag.class);
        mProgressDialogProvider = injector.getProvider(ClearFilterProgressDialog.class);
        mFilterCleanliness = injector.getInstance(FilterCleanliness.class);
    }

    public void onCreate() {
        mListActivity.setContentView(R.layout.cache_list);
        final ListView listView = mListActivity.getListView();
        listView.addHeaderView((View)mInflatedGpsStatusWidget.getTag());
        mListActivity.setListAdapter(mGeocacheListAdapter);
        listView.setOnCreateContextMenuListener(new CacheListOnCreateContextMenuListener(
                mGeocacheVectors));
        listView.setOnScrollListener(mScrollListener);

        // final List<Sensor> sensorList =
        // mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        // mCompassSensor = sensorList.get(0);
    }

    @Override
    public void onPause() {
        mCombinedLocationManager.removeUpdates();
        mSensorManagerWrapper.unregisterListener();
        mShakeWaker.unregister();
    }

    public void onResume(CacheListRefresh cacheListRefresh) {
        if (mFilterCleanliness.isDirty()) {
            ProgressDialog progressDialog = mProgressDialogProvider.get();
            progressDialog.incrementProgressBy(1);
            progressDialog.show();
            mUpdateFlag.setUpdatesEnabled(false);
            mUpdateFilterWorker.start();
        }

        final CacheListRefreshLocationListener cacheListRefreshLocationListener = new CacheListRefreshLocationListener(
                cacheListRefresh);
        final CacheListCompassListener mCompassListener = mCacheListCompassListenerProvider.get();
        mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0, mLocationControlBuffered);
        mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0, mCombinedLocationListener);
        mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0,
                cacheListRefreshLocationListener);

        mCombinedLocationManager.addGpsStatusListener(mGpsStatusListener);

        // mSensorManager.registerListener(mCompassListener, mCompassSensor,
        // SensorManager.SENSOR_DELAY_UI);
        mUpdateGpsWidgetRunnable.run();
        mSensorManagerWrapper.registerListener(mCompassListener, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        mShakeWaker.register();
        Log.d("GeoBeagle", "GeocacheListPresenter onResume done");
    }
}
