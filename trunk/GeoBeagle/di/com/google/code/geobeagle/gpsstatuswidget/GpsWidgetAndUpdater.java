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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;
import android.widget.TextView;

public class GpsWidgetAndUpdater {
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final UpdateGpsWidgetRunnable mUpdateGpsRunnable;

    @Inject
    public GpsWidgetAndUpdater(Context context, GpsStatusWidget gpsStatusWidget,
            CombinedLocationManager combinedLocationManager,
            Provider<DistanceFormatter> distanceFormatterProvider, Time time, MeterBars meterBars,
            Meter meter, TextLagUpdater textLagUpdater, UpdateGpsWidgetRunnable updateGpsRunnable) {
        mUpdateGpsRunnable = updateGpsRunnable;
        final TextView status = (TextView)gpsStatusWidget.findViewById(R.id.status);
        final TextView provider = (TextView)gpsStatusWidget.findViewById(R.id.provider);
        final MeterFader meterFader = new MeterFader(gpsStatusWidget, meterBars, time);
        mGpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(combinedLocationManager,
                distanceFormatterProvider, meter, meterFader, provider, context, status,
                textLagUpdater);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public UpdateGpsWidgetRunnable getUpdateGpsWidgetRunnable() {
        return mUpdateGpsRunnable;
    }
}
