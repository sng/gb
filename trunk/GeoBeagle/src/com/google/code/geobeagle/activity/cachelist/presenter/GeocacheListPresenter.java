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
import com.google.code.geobeagle.database.UpdateFilterWorker;
import com.google.code.geobeagle.gpsstatuswidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
    private final SharedPreferences mSharedPreferences;
    private final UpdateFilterWorker mUpdateFilterWorker;
    private final UpdateFlag mUpdateFlag;
    private final Provider<ClearFilterProgressDialog> mProgressDialogProvider;

    @Inject
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
            SharedPreferences sharedPreferences,
            UpdateFilterWorker updateFilterWorker,
            UpdateFlag updateFlag,
            Provider<ClearFilterProgressDialog> progressDialogProvider) {
        mCombinedLocationListener = combinedLocationListener;
        mCombinedLocationManager = combinedLocationManager;
        mCacheListCompassListenerProvider = cacheListCompassListenerProvider;
        mGeocacheListAdapter = geocacheListAdapter;
        mGeocacheVectors = geocacheVectors;
        mInflatedGpsStatusWidget = inflatedGpsStatusWidget;
        mListActivity = (ListActivity)listActivity;
        mLocationControlBuffered = locationControlBuffered;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mSensorManagerWrapper = sensorManagerWrapper;
        mScrollListener = scrollListener;
        mGpsStatusListener = gpsStatusListener;
        mSharedPreferences = sharedPreferences;
        mUpdateFilterWorker = updateFilterWorker;
        mUpdateFlag = updateFlag;
        mProgressDialogProvider = progressDialogProvider;
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
    }

    public void onResume(CacheListRefresh cacheListRefresh) {
        if (mSharedPreferences.getBoolean("filter-dirty", false)) {
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
        Log.d("GeoBeagle", "GeocacheListPresenter onResume done");
    }
}
