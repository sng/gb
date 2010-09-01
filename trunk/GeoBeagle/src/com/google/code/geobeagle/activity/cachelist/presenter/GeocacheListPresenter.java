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
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.UpdateFilterWorker;
import com.google.code.geobeagle.database.UpdateFilterWorker.DeterminateUpdateFilterProgressDialog;
import com.google.code.geobeagle.database.UpdateFilterWorker.IndeterminateUpdateFilterProgressDialog;
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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
    private final Provider<IndeterminateUpdateFilterProgressDialog> mProgressDialogProvider;

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
            DbFrontend dbFrontend,
            UpdateFilterWorker updateFilterWorker,
            UpdateFlag updateFlag,
            Provider<IndeterminateUpdateFilterProgressDialog> progressDialogProvider) {
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

    public static class UpdateFilterHandler extends Handler {
        private final Provider<IndeterminateUpdateFilterProgressDialog> bulkUpdateProgressDialogProvider;
        private final Provider<DeterminateUpdateFilterProgressDialog> incrementalUpdateProgressDialogProvider;
        private final Activity activity;
        private final CacheListRefresh cacheListRefresh;
        private final UpdateFlag updateFlag;

        @Inject
        UpdateFilterHandler(Provider<IndeterminateUpdateFilterProgressDialog> progressDialogProvider,
                Activity activity,
                CacheListRefresh cacheListRefresh,
                UpdateFlag updateFlag,
                Provider<DeterminateUpdateFilterProgressDialog> incrementalUpdateProgressDialogProvider) {
            this.bulkUpdateProgressDialogProvider = progressDialogProvider;
            this.incrementalUpdateProgressDialogProvider = incrementalUpdateProgressDialogProvider;
            this.activity = activity;
            this.cacheListRefresh = cacheListRefresh;
            this.updateFlag = updateFlag;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 4) {
                IndeterminateUpdateFilterProgressDialog updateFilterProgressDialog = bulkUpdateProgressDialogProvider
                        .get();
                Log.d("GeoBeagle", "dismissing " + updateFilterProgressDialog);
                activity.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, 10000);
                updateFilterProgressDialog.dismiss();
                updateFlag.setUpdatesEnabled(true);
                cacheListRefresh.forceRefresh();
            } else if (msg.what == 1) {
                DeterminateUpdateFilterProgressDialog determinateUpdateFilterProgressDialog = incrementalUpdateProgressDialogProvider
                        .get();
                Log.d("GeoBeagle", "dismissing " + bulkUpdateProgressDialogProvider);
                activity.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, 10000);
                determinateUpdateFilterProgressDialog.dismiss();
                updateFlag.setUpdatesEnabled(true);
                cacheListRefresh.forceRefresh();
            } else if (msg.what == 2) {
                DeterminateUpdateFilterProgressDialog determinateUpdateFilterProgressDialog = incrementalUpdateProgressDialogProvider
                        .get();
                IndeterminateUpdateFilterProgressDialog bulkUpdateFilterProgressDialog = bulkUpdateProgressDialogProvider
                        .get();
                bulkUpdateFilterProgressDialog.dismiss();
                determinateUpdateFilterProgressDialog.setMax(msg.arg1);
                determinateUpdateFilterProgressDialog.show();
                Log.d("GeoBeagle", "setting max " + msg.arg1);
            } else if (msg.what == 3) {
                DeterminateUpdateFilterProgressDialog determinateUpdateFilterProgressDialog = incrementalUpdateProgressDialogProvider
                        .get();
                determinateUpdateFilterProgressDialog.incrementProgressBy(1);
            }
        }
    }

    public void onResume(CacheListRefresh cacheListRefresh) {
        if (mSharedPreferences.getBoolean("filter-dirty", false)) {
            // mDbFrontend.updateFilter();
            ProgressDialog progressDialog = mProgressDialogProvider.get();
            progressDialog.incrementProgressBy(1);
            Log.d("GeoBeagle", "showing " + progressDialog);
            progressDialog.show();
            mListActivity.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, 5000);
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
