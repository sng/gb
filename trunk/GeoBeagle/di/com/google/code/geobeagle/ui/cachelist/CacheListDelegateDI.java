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

package com.google.code.geobeagle.ui.cachelist;

import com.google.code.geobeagle.GeoBeagle;
import com.google.code.geobeagle.GeoBeagleLocationListener;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.DistanceFormatter;
import com.google.code.geobeagle.data.GeocacheFactory;
import com.google.code.geobeagle.data.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.data.GeocacheVectorFactory;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.DatabaseDI;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.io.GpxImporter;
import com.google.code.geobeagle.io.GpxImporterDI;
import com.google.code.geobeagle.io.LocationSaver;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxToCacheDI.XmlPullParserWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GpsStatusWidget;
import com.google.code.geobeagle.ui.Misc;
import com.google.code.geobeagle.ui.UpdateGpsWidgetRunnableDI;
import com.google.code.geobeagle.ui.GpsStatusWidget.MeterFormatter;
import com.google.code.geobeagle.ui.GpsStatusWidget.MeterView;
import com.google.code.geobeagle.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CacheListDelegateDI {
    private static TextView getTextView(View gpsWidgetView, int id) {
        return (TextView)gpsWidgetView.findViewById(id);
    }

    public static CacheListDelegate create(ListActivity parent, LayoutInflater layoutInflater) {
        final ErrorDisplayer errorDisplayer = new ErrorDisplayer(parent);
        final Database database = DatabaseDI.create(parent);
        final LocationManager locationManager = (LocationManager)parent
                .getSystemService(Context.LOCATION_SERVICE);
        final LocationControl locationControl = LocationControlDi.create(locationManager);
        final GeocacheFactory geocacheFactory = new GeocacheFactory();
        final GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = new GeocacheFromMyLocationFactory(
                geocacheFactory, locationControl);
        final SQLiteWrapper sqliteWrapper = new SQLiteWrapper(null);
        final GeocachesSql locationBookmarks = DatabaseDI.create(locationControl, sqliteWrapper);
        final CacheWriter cacheWriter = DatabaseDI.createCacheWriter(sqliteWrapper);
        final DistanceFormatter distanceFormatter = new DistanceFormatter();
        final LocationSaver locationSaver = new LocationSaver(database, sqliteWrapper, cacheWriter);
        final GeocacheVectorFactory geocacheVectorFactory = new GeocacheVectorFactory(
                distanceFormatter);
        final LocationComparator locationComparator = new LocationComparator();
        final GeocacheVectors geocacheVectors = new GeocacheVectors(locationComparator,
                geocacheVectorFactory);
        final CacheListData cacheListData = new CacheListData(geocacheVectors);
        final XmlPullParserWrapper xmlPullParserWrapper = new XmlPullParserWrapper();
        final GpxImporter gpxImporter = GpxImporterDI.create(database, sqliteWrapper, parent,
                xmlPullParserWrapper, errorDisplayer);
        final View gpsWidgetView = layoutInflater.inflate(R.layout.gps_widget, null);

        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                layoutInflater, geocacheVectors);

        final GeocacheListAdapter geocacheListAdapter = new GeocacheListAdapter(geocacheVectors,
                geocacheSummaryRowInflater);
        GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(new ResourceProvider(parent),
                new MeterView(getTextView(gpsWidgetView, R.id.location_viewer),
                        new MeterFormatter()), getTextView(gpsWidgetView, R.id.provider),
                getTextView(gpsWidgetView, R.id.lag), getTextView(gpsWidgetView, R.id.accuracy),
                getTextView(gpsWidgetView, R.id.status), new Misc.Time(), new Location(""));
        final GeoBeagleLocationListener locationListener = new GeoBeagleLocationListener(
                locationControl, gpsStatusWidget);
        final UpdateGpsWidgetRunnable updateGpsWidgetRunnable = UpdateGpsWidgetRunnableDI
                .create(gpsStatusWidget);
        final ContextAction contextActions[] = CacheListDelegateDI.createContextActions(parent,
                database, sqliteWrapper, cacheListData, cacheWriter, geocacheVectors,
                errorDisplayer, geocacheListAdapter);

        final Handler handler = new Handler();
        final GeocacheListPresenter geocacheListPresenter = new GeocacheListPresenter(
                locationManager, locationControl, locationListener, gpsWidgetView,
                updateGpsWidgetRunnable, locationBookmarks, geocacheVectors, geocacheListAdapter,
                cacheListData, parent, handler, errorDisplayer, sqliteWrapper, database);
        final MenuActionSyncGpx menuActionSyncGpx = new MenuActionSyncGpx(gpxImporter,
                geocacheListPresenter);
        final MenuActionMyLocation menuActionMyLocation = new MenuActionMyLocation(locationSaver,
                geocacheFromMyLocationFactory, geocacheListPresenter, errorDisplayer);
        final MenuActionRefresh menuActionRefresh = new MenuActionRefresh(geocacheListPresenter);
        final MenuActions menuActions = new MenuActions(menuActionSyncGpx, menuActionMyLocation,
                menuActionRefresh);
        final GeocacheListController geocacheListController = new GeocacheListController(
                menuActions, contextActions, gpxImporter, errorDisplayer);
        return new CacheListDelegate(geocacheListController, geocacheListPresenter);
    }

    public static ContextAction[] createContextActions(ListActivity parent, Database database,
            SQLiteWrapper sqliteWrapper, CacheListData cacheListData, CacheWriter cacheWriter,
            GeocacheVectors geocacheVectors, ErrorDisplayer errorDisplayer,
            BaseAdapter geocacheListAdapter) {
        final Intent intent = new Intent(parent, GeoBeagle.class);
        final ContextActionView contextActionView = new ContextActionView(geocacheVectors, parent,
                intent);
        final ContextActionDelete contextActionDelete = new ContextActionDelete(
                geocacheListAdapter, cacheWriter, geocacheVectors);
        return new ContextAction[] {
                contextActionDelete, contextActionView
        };
    }
}
