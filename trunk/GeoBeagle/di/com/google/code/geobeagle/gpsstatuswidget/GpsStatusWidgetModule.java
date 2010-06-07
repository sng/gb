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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate.GpsStatusWidgetDelegateFactory;
import com.google.code.geobeagle.gpsstatuswidget.MeterFader.MeterFaderFactory;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class GpsStatusWidgetModule extends AbstractAndroidModule {
    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface LocationViewer {}
    
    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface AccuracyView {}

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface Lag {}
    @Override
    protected void configure() {
        bind(MeterFaderFactory.class).toProvider(
                FactoryProvider.newFactory(MeterFaderFactory.class, MeterFader.class));
        bind(GpsStatusWidgetDelegateFactory.class).toProvider(
                FactoryProvider.newFactory(GpsStatusWidgetDelegateFactory.class,
                        GpsStatusWidgetDelegate.class));
        bind(InflatedGpsStatusWidget.class).in(ContextScoped.class);
        bind(Meter.class).in(ContextScoped.class);
        bind(TextLagUpdater.class).in(ContextScoped.class);
    }

    @Provides
    @ContextScoped
    GpsStatusWidget providesGpsStatusWidget(InflatedGpsStatusWidget inflatedGpsStatusWidget,
            Context context) {
        GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(context);
        gpsStatusWidget.addView(inflatedGpsStatusWidget, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        return gpsStatusWidget;
    }

    @Provides
    @LocationViewer
    TextView providesLocationViewer(GpsStatusWidget gpsStatusWidget) {
        return (TextView)gpsStatusWidget.findViewById(R.id.location_viewer);
    }
    
    @Provides
    @AccuracyView
    TextView providesAccuracyView(GpsStatusWidget gpsStatusWidget) {
        return (TextView)gpsStatusWidget.findViewById(R.id.accuracy);
    }
    
    @Provides
    @Lag
    TextView providesLagView(GpsStatusWidget gpsStatusWidget) {
        return (TextView)gpsStatusWidget.findViewById(R.id.lag);
    }

}
