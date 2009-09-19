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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.CacheTypeFactory;
import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.LocationControlBuffered.GpsDisabledLocation;
import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.ActivityDI;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegate.ImportIntentManager;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextAction;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionEdit;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionView;
import com.google.code.geobeagle.activity.cachelist.actions.menu.Abortable;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionToggleFilter;
import com.google.code.geobeagle.activity.cachelist.model.CacheListData;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.ActionAndTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.AdapterCachesSorter;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManagerDi;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceUpdater;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.activity.cachelist.presenter.ListTitleFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.LocationAndAzimuthTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.LocationTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.RelativeBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.SensorManagerWrapper;
import com.google.code.geobeagle.activity.cachelist.presenter.SqlCacheLoader;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.activity.cachelist.presenter.ToleranceStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.ActionManager;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.activity.cachelist.view.GeocacheSummaryRowInflater;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.FilterNearestCaches;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.code.geobeagle.database.WhereFactoryAllCaches;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches;
import com.google.code.geobeagle.database.DatabaseDI.SearchFactory;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.WhereStringFactory;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.GpsWidgetAndUpdater;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.CachePersisterFacadeFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout.LayoutParams;

import java.util.ArrayList;
import java.util.Calendar;

public class CacheListDelegateDI {
    public static class Timing {
        private long mStartTime;

        public void lap(CharSequence msg) {
            long finishTime = Calendar.getInstance().getTimeInMillis();
            Log.d("GeoBeagle", "****** " + msg + ": " + (finishTime - mStartTime));
            mStartTime = finishTime;
        }

        public void start() {
            mStartTime = Calendar.getInstance().getTimeInMillis();
        }

        public long getTime() {
            return Calendar.getInstance().getTimeInMillis();
        }
    }

    public static CacheListDelegate create(ListActivity listActivity, LayoutInflater layoutInflater) {
        final OnClickListener mOnClickListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        };
        final ErrorDisplayer errorDisplayer = new ErrorDisplayer(listActivity, mOnClickListener);
        final LocationManager locationManager = (LocationManager)listActivity
                .getSystemService(Context.LOCATION_SERVICE);
        ArrayList<LocationListener> locationListeners = new ArrayList<LocationListener>(3);
        final CombinedLocationManager combinedLocationManager = new CombinedLocationManager(
                locationManager, locationListeners);
        final LocationControlBuffered locationControlBuffered = LocationControlDi
                .create(locationManager);
        final GeocacheFactory geocacheFactory = new GeocacheFactory();
        final GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = new GeocacheFromMyLocationFactory(
                geocacheFactory, locationControlBuffered);
        final BearingFormatter relativeBearingFormatter = new RelativeBearingFormatter();
        final DistanceFormatterManager distanceFormatterManager = DistanceFormatterManagerDi
                .create(listActivity);
        final ArrayList<GeocacheVector> geocacheVectorsList = new ArrayList<GeocacheVector>(10);
        final GeocacheVectors geocacheVectors = new GeocacheVectors(geocacheVectorsList);
        final CacheListData cacheListData = new CacheListData(geocacheVectors);
        final XmlPullParserWrapper xmlPullParserWrapper = new XmlPullParserWrapper();

        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                distanceFormatterManager.getFormatter(), geocacheVectors, layoutInflater,
                relativeBearingFormatter);
        final UpdateFlag updateFlag = new UpdateFlag();
        final GeocacheListAdapter geocacheListAdapter = new GeocacheListAdapter(geocacheVectors,
                geocacheSummaryRowInflater);

        final InflatedGpsStatusWidget inflatedGpsStatusWidget = new InflatedGpsStatusWidget(
                listActivity);
        final GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(listActivity);

        gpsStatusWidget.addView(inflatedGpsStatusWidget, LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        final GpsWidgetAndUpdater gpsWidgetAndUpdater = new GpsWidgetAndUpdater(listActivity,
                gpsStatusWidget, locationControlBuffered, combinedLocationManager,
                distanceFormatterManager.getFormatter());
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = gpsWidgetAndUpdater
                .getGpsStatusWidgetDelegate();

        inflatedGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);

        final CombinedLocationListener combinedLocationListener = new CombinedLocationListener(
                locationControlBuffered, gpsStatusWidgetDelegate);

        final UpdateGpsWidgetRunnable updateGpsWidgetRunnable = gpsWidgetAndUpdater
                .getUpdateGpsWidgetRunnable();
        updateGpsWidgetRunnable.run();

        final WhereFactoryAllCaches whereFactoryAllCaches = new WhereFactoryAllCaches();
        final SearchFactory searchFactory = new SearchFactory();
        final WhereStringFactory whereStringFactory = new WhereStringFactory();
        final WhereFactoryNearestCaches whereFactoryNearestCaches = new WhereFactoryNearestCaches(
                searchFactory, whereStringFactory);

        final FilterNearestCaches filterNearestCaches = new FilterNearestCaches(
                whereFactoryAllCaches, whereFactoryNearestCaches);
        final ListTitleFormatter listTitleFormatter = new ListTitleFormatter();
        final CacheListDelegateDI.Timing timing = new CacheListDelegateDI.Timing();

        final AdapterCachesSorter adapterCachesSorter = new AdapterCachesSorter(cacheListData,
                timing, locationControlBuffered);
        final GpsDisabledLocation gpsDisabledLocation = new GpsDisabledLocation();
        final DistanceUpdater distanceUpdater = new DistanceUpdater(geocacheListAdapter);
        final ToleranceStrategy sqlCacheLoaderTolerance = new LocationTolerance(500,
                gpsDisabledLocation, 1000);
        final ToleranceStrategy adapterCachesSorterTolerance = new LocationTolerance(6,
                gpsDisabledLocation, 1000);
        final LocationTolerance distanceUpdaterLocationTolerance = new LocationTolerance(1,
                gpsDisabledLocation, 1000);
        final ToleranceStrategy distanceUpdaterTolerance = new LocationAndAzimuthTolerance(
                distanceUpdaterLocationTolerance, 720);
        final ActionAndTolerance[] actionAndTolerances = new ActionAndTolerance[] {
                null, new ActionAndTolerance(adapterCachesSorter, adapterCachesSorterTolerance),
                new ActionAndTolerance(distanceUpdater, distanceUpdaterTolerance)
        };
        final ActionManagerFactory actionManagerFactory = new ActionManagerFactory(
                actionAndTolerances, sqlCacheLoaderTolerance);

        final DbFrontend dbFrontend = new DbFrontend(listActivity);
        final TitleUpdater titleUpdater = new TitleUpdater(listActivity, filterNearestCaches,
                listTitleFormatter, timing);
        final SqlCacheLoader sqlCacheLoader = new SqlCacheLoader(dbFrontend, filterNearestCaches,
                cacheListData, locationControlBuffered, titleUpdater, timing);
        final ActionManager actionManager = actionManagerFactory.create(sqlCacheLoader);
        final CacheListRefresh cacheListRefresh = new CacheListRefresh(actionManager, timing,
                locationControlBuffered, updateFlag);

        final SensorManager sensorManager = (SensorManager)listActivity
                .getSystemService(Context.SENSOR_SERVICE);
        final CompassListenerFactory compassListenerFactory = new CompassListenerFactory(
                locationControlBuffered);

        distanceFormatterManager.addHasDistanceFormatter(geocacheSummaryRowInflater);
        distanceFormatterManager.addHasDistanceFormatter(gpsStatusWidgetDelegate);
        final CacheListView.ScrollListener scrollListener = new CacheListView.ScrollListener(
                updateFlag);
        final SensorManagerWrapper sensorManagerWrapper = new SensorManagerWrapper(sensorManager);
        final GeocacheListPresenter geocacheListPresenter = new GeocacheListPresenter(
                combinedLocationListener, combinedLocationManager, compassListenerFactory,
                distanceFormatterManager, geocacheListAdapter, geocacheSummaryRowInflater,
                geocacheVectors, gpsStatusWidget, listActivity, locationControlBuffered,
                sensorManagerWrapper, updateGpsWidgetRunnable, scrollListener);
        final CacheTypeFactory cacheTypeFactory = new CacheTypeFactory();

        final Aborter aborter = new Aborter();
        final MessageHandler messageHandler = MessageHandler.create(listActivity);
        final CachePersisterFacadeFactory cachePersisterFacadeFactory = new CachePersisterFacadeFactory(
                messageHandler, cacheTypeFactory);

        final GpxImporterFactory gpxImporterFactory = new GpxImporterFactory(aborter,
                cachePersisterFacadeFactory, errorDisplayer, geocacheListPresenter, listActivity,
                messageHandler, xmlPullParserWrapper);

        final Abortable nullAbortable = new Abortable() {
            public void abort() {
            }
        };

        final MenuActionSyncGpx menuActionSyncGpx = new MenuActionSyncGpx(nullAbortable,
                cacheListRefresh, gpxImporterFactory, dbFrontend);
        final MenuActions menuActions = new MenuActions(listActivity.getResources());
        menuActions.add(menuActionSyncGpx);
        menuActions.add(new MenuActionToggleFilter(filterNearestCaches, cacheListRefresh));
        menuActions.add(new MenuActionMyLocation(cacheListRefresh, errorDisplayer,
                geocacheFromMyLocationFactory, new LocationSaver(dbFrontend)));
        menuActions.add(new MenuActionSearchOnline(listActivity));
        // menuActions.add(new MenuActionChooseFilter(listActivity));

        final Intent geoBeagleMainIntent = new Intent(listActivity, GeoBeagle.class);
        final ContextActionView contextActionView = new ContextActionView(geocacheVectors,
                listActivity, geoBeagleMainIntent);
        final ContextActionEdit contextActionEdit = new ContextActionEdit(geocacheVectors,
                listActivity);
        final ContextActionDelete contextActionDelete = new ContextActionDelete(
                geocacheListAdapter, geocacheVectors, titleUpdater, dbFrontend);

        final ContextAction[] contextActions = new ContextAction[] {
                contextActionDelete, contextActionView, contextActionEdit
        };
        final GeocacheListController geocacheListController = new GeocacheListController(
                cacheListRefresh, contextActions, filterNearestCaches, menuActionSyncGpx,
                menuActions);

        final ActivitySaver activitySaver = ActivityDI.createActivitySaver(listActivity);
        final ImportIntentManager importIntentManager = new ImportIntentManager(listActivity);
        return new CacheListDelegate(importIntentManager, activitySaver, cacheListRefresh,
                geocacheListController, geocacheListPresenter, dbFrontend);
    }
}
