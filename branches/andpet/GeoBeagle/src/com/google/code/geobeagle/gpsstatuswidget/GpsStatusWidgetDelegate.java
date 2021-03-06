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

import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.cachelist.presenter.HasDistanceFormatter;
import com.google.code.geobeagle.formatting.DistanceFormatter;

import android.content.Context;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.TextView;

public class GpsStatusWidgetDelegate implements HasDistanceFormatter, Refresher  {
    private final Context mContext;
    private DistanceFormatter mDistanceFormatter;
    private final GeoFixProvider mGeoFixProvider;
    private final MeterFader mMeterFader;
    private final Meter mMeter;
    private final TextView mProvider;
    private final TextView mStatus;
    private final TextView mLagTextView;
    private GeoFix mGeoFix;
    private final TimeProvider mTimeProvider;

    public static interface TimeProvider {
        long getTime();
    }
    
    public GpsStatusWidgetDelegate(GeoFixProvider geoFixProvider,
            DistanceFormatter distanceFormatter, Meter meter,
            MeterFader meterFader, TextView provider, Context context,
            TextView status, TextView lagTextView,
            GeoFix initialGeoFix, TimeProvider timeProvider) {
        mGeoFixProvider = geoFixProvider;
        mDistanceFormatter = distanceFormatter;
        mMeterFader = meterFader;
        mMeter = meter;
        mProvider = provider;
        mContext = context;
        mStatus = status;
        mLagTextView = lagTextView;
        mGeoFix = initialGeoFix;
        mTimeProvider = timeProvider;
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                mStatus.setText(provider + " status: "
                        + mContext.getString(R.string.out_of_service));
                break;
            case LocationProvider.AVAILABLE:
                mStatus.setText(provider + " status: "
                        + mContext.getString(R.string.available));
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

    /** Called when the location changed */
    public void refresh() {
        mGeoFix = mGeoFixProvider.getLocation();
        //Log.d("GeoBeagle", "GpsStatusWidget onLocationChanged " + mGeoFix);

        /*
        if (!mGeoFixProvider.isProviderEnabled()) {
            mMeter.setDisabled();
            mTextLagUpdater.setDisabled();
            return;
        }
        */
        mProvider.setText(mGeoFix.getProvider());
        mMeter.setAccuracy(mGeoFix.getAccuracy(), mDistanceFormatter);
        mMeterFader.reset();
        mLagTextView.setText(mGeoFix.getLagString(mTimeProvider.getTime()));
    }

    public void setDistanceFormatter(DistanceFormatter distanceFormatter) {
        mDistanceFormatter = distanceFormatter;
    }
    
    public void updateLagText(long systemTime) {
        mLagTextView.setText(mGeoFix.getLagString(systemTime));
    }

    @Override
    public void forceRefresh() {
        refresh();
    }
}
