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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.main.view.CheckDetailsButton;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.WebPageMenuEnabler;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.List;

public class GeoBeagleDelegate {
    static class ForceThresholdStrategy {
        public boolean exceedsThreshold(float[] values) {
            double totalForce = 0.0f;
            totalForce += Math.pow(values[SensorManager.DATA_X] / SensorManager.GRAVITY_EARTH, 2.0);
            totalForce += Math.pow(values[SensorManager.DATA_Y] / SensorManager.GRAVITY_EARTH, 2.0);
            totalForce += Math.pow(values[SensorManager.DATA_Z] / SensorManager.GRAVITY_EARTH, 2.0);
            totalForce = Math.sqrt(totalForce);
            double abs = Math.abs(1.0 - totalForce);
            return abs > 0.1;
        }
    }

    static class ShakeListener implements SensorEventListener {
        private final PowerManager pm;
        private final ForceThresholdStrategy forceThresholdStrategy;

        @Inject
        ShakeListener(PowerManager pm, ForceThresholdStrategy forceThresholdStrategy) {
            this.pm = pm;
            this.forceThresholdStrategy = forceThresholdStrategy;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (forceThresholdStrategy.exceedsThreshold(event.values)) {
                WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE, "accel");
                wakeLock.acquire(5000);
                Log.d("GeoBeagle", "shaked; wakelocking: " + event.values[0] + ", "
                        + event.values[1] + ", " + event.values[2]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    static class GeoBeagleSensors {
        private final SensorManager sensorManager;
        private final ShakeListener shakeListener;

        @Inject
        GeoBeagleSensors(SensorManager sensorManager, ShakeListener shakeListener) {
            this.sensorManager = sensorManager;
            this.shakeListener = shakeListener;
        }

        public void registerSensors() {
            List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (sensorList.size() > 0)
                sensorManager.registerListener(shakeListener, sensorList.get(0),
                        SensorManager.SENSOR_DELAY_UI);
        }

        public void unregisterSensors() {
            sensorManager.unregisterListener(shakeListener);
        }
    }

    static int ACTIVITY_REQUEST_TAKE_PICTURE = 1;
    private final ActivitySaver mActivitySaver;
    private final AppLifecycleManager mAppLifecycleManager;
    private final CompassListener mCompassListener;
    private final Provider<DbFrontend> mDbFrontendProvider;
    private Geocache mGeocache;
    private final GeocacheFactory mGeocacheFactory;
    private final GeocacheFromParcelFactory mGeocacheFromParcelFactory;
    private final GeocacheViewer mGeocacheViewer;
    private final IncomingIntentHandler mIncomingIntentHandler;
    private final GeoBeagleActivityMenuActions mMenuActions;
    private final GeoBeagle mParent;
    private final RadarView mRadarView;
    private final SensorManager mSensorManager;
    private final SharedPreferences mSharedPreferences;
    private final CheckDetailsButton mCheckDetailsButton;
    private final GeoBeagleEnvironment mGeoBeagleEnvironment;
    private final WebPageMenuEnabler mWebPageMenuEnabler;
    private final LocationSaver mLocationSaver;
    private final GeoBeagleSensors mGeoBeagleSensors;

    public GeoBeagleDelegate(ActivitySaver activitySaver,
            AppLifecycleManager appLifecycleManager,
            CompassListener compassListener,
            Activity parent,
            GeocacheFactory geocacheFactory,
            GeocacheViewer geocacheViewer,
            IncomingIntentHandler incomingIntentHandler,
            GeoBeagleActivityMenuActions menuActions,
            GeocacheFromParcelFactory geocacheFromParcelFactory,
            Provider<DbFrontend> dbFrontendProvider,
            RadarView radarView,
            SensorManager sensorManager,
            SharedPreferences sharedPreferences,
            CheckDetailsButton checkDetailsButton,
            WebPageMenuEnabler webPageMenuEnabler,
            GeoBeagleEnvironment geoBeagleEnvironment,
            LocationSaver locationSaver,
            GeoBeagleSensors geoBeagleSensors) {
        mParent = (GeoBeagle)parent;
        mActivitySaver = activitySaver;
        mAppLifecycleManager = appLifecycleManager;
        mMenuActions = menuActions;
        mSharedPreferences = sharedPreferences;
        mRadarView = radarView;
        mCompassListener = compassListener;
        mSensorManager = sensorManager;
        mGeocacheViewer = geocacheViewer;
        mGeocacheFactory = geocacheFactory;
        mIncomingIntentHandler = incomingIntentHandler;
        mDbFrontendProvider = dbFrontendProvider;
        mGeocacheFromParcelFactory = geocacheFromParcelFactory;
        mGeoBeagleEnvironment = geoBeagleEnvironment;
        mCheckDetailsButton = checkDetailsButton;
        mWebPageMenuEnabler = webPageMenuEnabler;
        mLocationSaver = locationSaver;
        mGeoBeagleSensors = geoBeagleSensors;
    }

    @Inject
    public GeoBeagleDelegate(Injector injector) {
        mParent = (GeoBeagle)injector.getInstance(Activity.class);
        mActivitySaver = injector.getInstance(ActivitySaver.class);
        mAppLifecycleManager = injector.getInstance(AppLifecycleManager.class);
        mMenuActions = injector.getInstance(GeoBeagleActivityMenuActions.class);
        mSharedPreferences = injector.getInstance(SharedPreferences.class);
        mRadarView = injector.getInstance(RadarView.class);
        mCompassListener = injector.getInstance(CompassListener.class);
        mSensorManager = injector.getInstance(SensorManager.class);
        mGeocacheViewer = injector.getInstance(GeocacheViewer.class);
        mGeocacheFactory = injector.getInstance(GeocacheFactory.class);
        mIncomingIntentHandler = injector.getInstance(IncomingIntentHandler.class);
        mDbFrontendProvider = injector.getProvider(DbFrontend.class);
        mGeocacheFromParcelFactory = injector.getInstance(GeocacheFromParcelFactory.class);
        mGeoBeagleEnvironment = injector.getInstance(GeoBeagleEnvironment.class);
        mCheckDetailsButton = injector.getInstance(CheckDetailsButton.class);
        mWebPageMenuEnabler = injector.getInstance(WebPageMenuEnabler.class);
        mLocationSaver = injector.getInstance(LocationSaver.class);
        mGeoBeagleSensors = injector.getInstance(GeoBeagleSensors.class);
    }

    public Geocache getGeocache() {
        return mGeocache;
    }

    private void onCameraStart() {
        String filename = mGeoBeagleEnvironment.getExternalStorageDir() + "/GeoBeagle_"
                + mGeocache.getId()
                + DateFormat.format("_yyyy-MM-dd_kk.mm.ss.jpg", System.currentTimeMillis());
        Log.d("GeoBeagle", "capturing image to " + filename);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filename)));
        mParent.startActivityForResult(intent, GeoBeagleDelegate.ACTIVITY_REQUEST_TAKE_PICTURE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenuActions.onCreateOptionsMenu(menu);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA && event.getRepeatCount() == 0) {
            onCameraStart();
            return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuActions.act(item.getItemId());
    }

    public void onPause() {
        mAppLifecycleManager.onPause();
        mGeoBeagleSensors.unregisterSensors();
        mActivitySaver.save(ActivityType.VIEW_CACHE, mGeocache);
        mSensorManager.unregisterListener(mRadarView);
        mSensorManager.unregisterListener(mCompassListener);
        mDbFrontendProvider.get().closeDatabase();
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mGeocache = mGeocacheFromParcelFactory.createFromBundle(savedInstanceState);
        // Is this really needed???
        // mWritableDatabase =
        // mGeoBeagleSqliteOpenHelper.getWritableSqliteWrapper();
    }

    public void onResume() {
        mRadarView.handleUnknownLocation();

        mRadarView.setUseImperial(mSharedPreferences.getBoolean("imperial", false));
        mAppLifecycleManager.onResume();
        mSensorManager.registerListener(mRadarView, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mCompassListener, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        mGeoBeagleSensors.registerSensors();
        mGeocache = mIncomingIntentHandler.maybeGetGeocacheFromIntent(mParent.getIntent(),
                mGeocache, mLocationSaver);

        // Possible fix for issue 53.
        if (mGeocache == null) {
            mGeocache = mGeocacheFactory.create("", "", 0, 0, Source.MY_LOCATION, "",
                    CacheType.NULL, 0, 0, 0, true, false);
        }
        mGeocacheViewer.set(mGeocache);
        mCheckDetailsButton.check(mGeocache);
    }

    public void onSaveInstanceState(Bundle outState) {
        // apparently there are cases where getGeocache returns null, causing
        // crashes with 0.7.7/0.7.8.
        if (mGeocache != null)
            mGeocache.saveToBundle(outState);
    }

    public void setGeocache(Geocache geocache) {
        mGeocache = geocache;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.string.web_page);
        item.setVisible(mWebPageMenuEnabler.shouldEnable(getGeocache()));
        return true;
    }
}
