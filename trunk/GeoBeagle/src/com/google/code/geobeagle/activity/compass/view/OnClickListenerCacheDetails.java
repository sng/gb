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

package com.google.code.geobeagle.activity.compass.view;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.CompassFragment;
import com.google.code.geobeagle.activity.details.DetailsActivity;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class OnClickListenerCacheDetails implements View.OnClickListener {

    private final Activity activity;

    // For testing.
    public OnClickListenerCacheDetails(Activity geoBeagle) {
        this.activity = geoBeagle;
    }

    @Inject
    public OnClickListenerCacheDetails(Injector injector) {
        activity = injector.getInstance(Activity.class);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, DetailsActivity.class);
        CompassFragment fragment = (CompassFragment)activity.getFragmentManager().findFragmentById(
                R.id.compass_frame);

        Geocache geocache = fragment.getGeocache();
        intent.putExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_SOURCE, geocache.getSourceName());
        intent.putExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_ID, geocache.getId().toString());
        intent.putExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_NAME, geocache.getName().toString());

        activity.startActivity(intent);
    }
}
