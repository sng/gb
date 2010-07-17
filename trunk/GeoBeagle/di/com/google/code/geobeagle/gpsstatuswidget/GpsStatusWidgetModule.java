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
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class GpsStatusWidgetModule extends AbstractAndroidModule {
    @Override
    protected void configure() {
    }

    @Provides
    @ContextScoped
    LinearLayout providesGpsStatusWidget(Context context) {
        return new LinearLayout(context);
    }

    @Provides
    @ContextScoped
    InflatedGpsStatusWidget providesInflatedGpsStatusWidget(Activity activity, Context context, LinearLayout gpsStatusWidget) {
        InflatedGpsStatusWidget inflatedGpsStatusWidget = (InflatedGpsStatusWidget)activity
                .findViewById(R.id.gps_widget_view);
        if (inflatedGpsStatusWidget != null)
            return inflatedGpsStatusWidget;

        inflatedGpsStatusWidget = new InflatedGpsStatusWidget(context);
        gpsStatusWidget.addView(inflatedGpsStatusWidget, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        return inflatedGpsStatusWidget;
    }
}
