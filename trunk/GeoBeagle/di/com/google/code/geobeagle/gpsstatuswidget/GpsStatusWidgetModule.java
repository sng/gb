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

package com.google.code.geobeagle.gpsstatuswidget;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.activity.cachelist.ActivityVisible;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate.GpsStatusWidgetDelegateFactory;
import com.google.code.geobeagle.gpsstatuswidget.MeterBars.MeterBarsFactory;
import com.google.code.geobeagle.gpsstatuswidget.MeterFader.MeterFaderFactory;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.TextLagUpdaterFactory;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable.UpdateGpsWidgetRunnableFactory;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class GpsStatusWidgetModule extends AbstractAndroidModule {
    @BindingAnnotation @Target( { FIELD, PARAMETER, METHOD })
    @Retention(RUNTIME) public static @interface SearchOnline { }

    @BindingAnnotation @Target( { FIELD, PARAMETER, METHOD })
    @Retention(RUNTIME) public static @interface CacheList { }

    @Override
    protected void configure() {
        bind(MeterBarsFactory.class).toProvider(
                FactoryProvider.newFactory(MeterBarsFactory.class, MeterBars.class));
        bind(TextLagUpdaterFactory.class).toProvider(
                FactoryProvider.newFactory(TextLagUpdaterFactory.class, TextLagUpdater.class));
        bind(UpdateGpsWidgetRunnableFactory.class).toProvider(
                FactoryProvider.newFactory(UpdateGpsWidgetRunnableFactory.class,
                        UpdateGpsWidgetRunnable.class));
        bind(MeterFaderFactory.class).toProvider(
                FactoryProvider.newFactory(MeterFaderFactory.class, MeterFader.class));
        bind(GpsStatusWidgetDelegateFactory.class).toProvider(
                FactoryProvider.newFactory(GpsStatusWidgetDelegateFactory.class,
                        GpsStatusWidgetDelegate.class));
    }

    @Provides
    @SearchOnline
    @ContextScoped
    InflatedGpsStatusWidget providesInflatedGpsStatusWidgetSearch(Activity activity) {
        return (InflatedGpsStatusWidget)activity.findViewById(R.id.gps_widget_view);
    }
    
    @Provides
    @SearchOnline
    GpsWidgetAndUpdater providesGpsWidgetAndUpdaterSearch(Context context,
            LocationControlBuffered locationControlBuffered,
            CombinedLocationManager combinedLocationManager,
            Provider<DistanceFormatter> distanceFormatterProvider, ActivityVisible activityVisible,
            Time time, Handler handler, @SearchOnline InflatedGpsStatusWidget gpsWidgetView) {
        return new GpsWidgetAndUpdater(context, gpsWidgetView, locationControlBuffered,
                combinedLocationManager, distanceFormatterProvider, activityVisible, time, handler);
    }

    
    @Provides
    @CacheList
    @ContextScoped
    InflatedGpsStatusWidget providesInflatedGpsStatusWidgetCacheList(Activity activity) {
        return new InflatedGpsStatusWidget(activity);
    }

    @Provides
    @CacheList
    GpsWidgetAndUpdater providesGpsWidgetAndUpdaterCacheList(Context context,
            LocationControlBuffered locationControlBuffered,
            CombinedLocationManager combinedLocationManager,
            Provider<DistanceFormatter> distanceFormatterProvider, ActivityVisible activityVisible,
            Time time, Handler handler, @CacheList InflatedGpsStatusWidget gpsWidgetView) {
        return new GpsWidgetAndUpdater(context, gpsWidgetView, locationControlBuffered,
                combinedLocationManager, distanceFormatterProvider, activityVisible, time, handler);
    }

    @Provides
    GpsStatusWidget providesGpsStatusWidget(@CacheList InflatedGpsStatusWidget inflatedGpsStatusWidget,
            Context context) {
        GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(context);
        gpsStatusWidget.addView(inflatedGpsStatusWidget, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        return gpsStatusWidget;
    }
}
