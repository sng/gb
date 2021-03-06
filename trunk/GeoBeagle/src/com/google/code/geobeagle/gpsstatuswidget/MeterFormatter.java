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
import com.google.inject.Inject;

import android.content.Context;

class MeterFormatter {
    private static String mMeterLeft;
    private static String mMeterRight;
    private static String mDegreesSymbol;
    private static StringBuilder mStringBuilder;

    @Inject
    MeterFormatter(Context context) {
        mMeterLeft = context.getString(R.string.meter_left);
        mMeterRight = context.getString(R.string.meter_right);
        mDegreesSymbol = context.getString(R.string.degrees_symbol);
        mStringBuilder = new StringBuilder();
    }

    int accuracyToBarCount(float accuracy) {
        return Math.min(mMeterLeft.length(), (int)(Math.log(Math.max(1, accuracy)) / Math.log(2)));
    }

    String barsToMeterText(int bars, String center) {
        mStringBuilder.setLength(0);
        mStringBuilder.append('[').append(mMeterLeft.substring(mMeterLeft.length() - bars)).append(
                center + mDegreesSymbol).append(mMeterRight.substring(0, bars)).append(']');
        return mStringBuilder.toString();
    }

    int lagToAlpha(long milliseconds) {
        return Math.max(128, 255 - (int)(milliseconds >> 3));
    }
}
