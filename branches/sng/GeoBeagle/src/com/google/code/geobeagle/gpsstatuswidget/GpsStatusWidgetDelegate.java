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
import com.google.code.geobeagle.activity.cachelist.presenter.HasDistanceFormatter;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.location.CombinedLocationManager;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.TextView;

public class GpsStatusWidgetDelegate implements HasDistanceFormatter, LocationListener {
    private final CombinedLocationManager mCombinedLocationManager;
    private DistanceFormatter mDistanceFormatter;
    private final MeterFader mMeterFader;
    private final Meter mMeterWrapper;
    private final TextView mProvider;
    private final Context mContext;
    private final TextView mStatus;
    private final TextLagUpdater mTextLagUpdater;

    public GpsStatusWidgetDelegate(CombinedLocationManager combinedLocationManager,
            DistanceFormatter distanceFormatter, Meter meter, MeterFader meterFader,
            TextView provider, Context context, TextView status, TextLagUpdater textLagUpdater) {
        mCombinedLocationManager = combinedLocationManager;
        mDistanceFormatter = distanceFormatter;
        mMeterFader = meterFader;
        mMeterWrapper = meter;
        mProvider = provider;
        mContext = context;
        mStatus = status;
        mTextLagUpdater = textLagUpdater;
    }

    public void onLocationChanged(Location location) {
        // Log.d("GeoBeagle", "GpsStatusWidget onLocationChanged " + location);
        if (location == null)
            return;

        if (!mCombinedLocationManager.isProviderEnabled()) {
            mMeterWrapper.setDisabled();
            mTextLagUpdater.setDisabled();
            return;
        }
        mProvider.setText(location.getProvider());
        mMeterWrapper.setAccuracy(location.getAccuracy(), mDistanceFormatter);
        mMeterFader.reset();
        mTextLagUpdater.reset(location.getTime());
    }

    public void onProviderDisabled(String provider) {
        mStatus.setText(provider + " DISABLED");
    }

    public void onProviderEnabled(String provider) {
        mStatus.setText(provider + " ENABLED");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                mStatus.setText(provider + " status: "
                        + mContext.getString(R.string.out_of_service));
                break;
            case LocationProvider.AVAILABLE:
                mStatus.setText(provider + " status: " + mContext.getString(R.string.available));
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                mStatus.setText(provider + " status: "
                        + mContext.getString(R.string.temporarily_unavailable));
                break;
        }
    }

    public void paint() {
        mMeterFader.paint();
    }

    public void setDistanceFormatter(DistanceFormatter distanceFormatter) {
        mDistanceFormatter = distanceFormatter;
    }
}
