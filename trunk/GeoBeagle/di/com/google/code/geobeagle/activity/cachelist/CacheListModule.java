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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.LocationControlBuffered.GpsDisabledLocation;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegate.CacheListDelegateFactory;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.GeocacheListControllerFactory;
import com.google.code.geobeagle.activity.cachelist.GpxImporterFactory.GpxImporterFactoryFactory;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete;
import com.google.code.geobeagle.activity.cachelist.actions.menu.Abortable;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionDeleteAllCaches;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncBCaching;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionDeleteAllCaches.MenuActionDeleteAllCachesFactory;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx.MenuActionSyncGpxFactory;
import com.google.code.geobeagle.activity.cachelist.model.CacheListData;
import com.google.code.geobeagle.activity.cachelist.presenter.AbsoluteBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.ActionAndTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.AdapterCachesSorter;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceUpdater;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.activity.cachelist.presenter.LocationAndAzimuthTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.LocationTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.RelativeBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.SqlCacheLoader;
import com.google.code.geobeagle.activity.cachelist.presenter.ToleranceStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.ActionManager;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter.GeocacheListPresenterFactory;
import com.google.code.geobeagle.activity.main.GeoBeagleModule.DefaultSharedPreferences;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.formatting.DistanceFormatterImperial;
import com.google.code.geobeagle.formatting.DistanceFormatterMetric;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.CachePersisterFacadeFactory;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.CachePersisterFacadeFactory.CachePersisterFacadeFactoryFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class CacheListModule extends AbstractAndroidModule {
    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface ToasterSyncAborted {}

    @Override
    protected void configure() {
        bind(GeocacheListAdapter.class).in(ContextScoped.class);
        bind(GeocacheListPresenterFactory.class).toProvider(
                FactoryProvider.newFactory(GeocacheListPresenterFactory.class,
                        GeocacheListPresenter.class));
        bind(MenuActionSyncBCaching.class).in(ContextScoped.class);
        bind(ActivityVisible.class).in(Singleton.class);
        bind(DistanceFormatter.class).toProvider(DistanceFormatterProvider.class).in(
                ContextScoped.class);
        bind(BearingFormatter.class).toProvider(BearingFormatterProvider.class).in(
                ContextScoped.class);
        bind(CacheListData.class).in(ContextScoped.class);
        bind(CacheListDelegateDI.Timing.class).in(Singleton.class);
        bind(UpdateFlag.class).in(Singleton.class);
        bind(CacheListRefresh.class).in(ContextScoped.class);
        bind(CachePersisterFacadeFactoryFactory.class).toProvider(
                FactoryProvider.newFactory(CachePersisterFacadeFactoryFactory.class,
                        CachePersisterFacadeFactory.class));
        bind(GpxImporterFactoryFactory.class).toProvider(
                FactoryProvider.newFactory(GpxImporterFactoryFactory.class,
                        GpxImporterFactory.class));
        bind(MenuActionSyncGpxFactory.class)
                .toProvider(
                        FactoryProvider.newFactory(MenuActionSyncGpxFactory.class,
                                MenuActionSyncGpx.class));
        bind(MenuActionDeleteAllCachesFactory.class).toProvider(
                FactoryProvider.newFactory(MenuActionDeleteAllCachesFactory.class,
                        MenuActionDeleteAllCaches.class));
        bind(ContextActionDelete.class).in(ContextScoped.class);
        bind(GeocacheListControllerFactory.class).toProvider(
                FactoryProvider.newFactory(GeocacheListControllerFactory.class,
                        GeocacheListController.class));
        bind(CacheListDelegateFactory.class)
                .toProvider(
                        FactoryProvider.newFactory(CacheListDelegateFactory.class,
                                CacheListDelegate.class));
    }

    static class DistanceFormatterProvider implements Provider<DistanceFormatter> {
        private final SharedPreferences preferenceManager;
        private final DistanceFormatterMetric distanceFormatterMetric;
        private final DistanceFormatterImperial distanceFormatterImperial;

        @Inject
        DistanceFormatterProvider(@DefaultSharedPreferences SharedPreferences preferenceManager,
                DistanceFormatterMetric distanceFormatterMetric,
                DistanceFormatterImperial distanceFormatterImperial) {
            this.preferenceManager = preferenceManager;
            this.distanceFormatterMetric = distanceFormatterMetric;
            this.distanceFormatterImperial = distanceFormatterImperial;
        }

        @Override
        public DistanceFormatter get() {
            return preferenceManager.getBoolean("imperial", false) ? distanceFormatterImperial
                    : distanceFormatterMetric;
        }
    }
    
    static class BearingFormatterProvider implements Provider<BearingFormatter> {
        private AbsoluteBearingFormatter absoluteBearingFormatter;
        private RelativeBearingFormatter relativeBearingFormatter;
        private final SharedPreferences preferenceManager;

        @Inject
        BearingFormatterProvider(@DefaultSharedPreferences SharedPreferences preferenceManager,
                AbsoluteBearingFormatter absoluteBearingFormatter,
                RelativeBearingFormatter relativeBearingFormatter) {
            this.preferenceManager = preferenceManager;
            this.absoluteBearingFormatter = absoluteBearingFormatter;
            this.relativeBearingFormatter = relativeBearingFormatter;
        }

        @Override
        public BearingFormatter get() {
            return preferenceManager.getBoolean("absolute-bearing", false) ? absoluteBearingFormatter
                    : relativeBearingFormatter;
        }
    }

    @Provides
    ListActivity providesListActivity(Activity activity) {
        return (ListActivity)activity;
    }

    @Provides
    ActionManager providesActionManager(GpsDisabledLocation gpsDisabledLocation,
            AdapterCachesSorter adapterCachesSorter, DistanceUpdater distanceUpdater,
            SqlCacheLoader sqlCacheLoader) {
        final ToleranceStrategy sqlCacheLoaderTolerance = new LocationTolerance(500,
                gpsDisabledLocation, 1000);
        final ToleranceStrategy adapterCachesSorterTolerance = new LocationTolerance(6,
                gpsDisabledLocation, 1000);
        final LocationTolerance distanceUpdaterLocationTolerance = new LocationTolerance(1,
                gpsDisabledLocation, 1000);
        final ToleranceStrategy distanceUpdaterTolerance = new LocationAndAzimuthTolerance(
                distanceUpdaterLocationTolerance, 720);

        final ActionAndTolerance[] actionAndTolerances = new ActionAndTolerance[] {
                new ActionAndTolerance(sqlCacheLoader, sqlCacheLoaderTolerance),
                new ActionAndTolerance(adapterCachesSorter, adapterCachesSorterTolerance),
                new ActionAndTolerance(distanceUpdater, distanceUpdaterTolerance)
        };

        return new ActionManager(actionAndTolerances);
    }

    @Provides
    @ToasterSyncAborted
    Toaster toasterProvider(Context context) {
        return new Toaster(context, R.string.import_canceled, Toast.LENGTH_LONG);
    }

    @Provides
    Abortable providesAbortable() {
        return new NullAbortable();
    }
    
}
