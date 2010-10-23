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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.inject.Inject;
import com.google.inject.Injector;

import android.util.Log;

public class DistanceUpdater implements RefreshAction {
    private final GeocacheListAdapter mGeocacheListAdapter;

    @Inject
    public DistanceUpdater(Injector injector) {
        mGeocacheListAdapter = injector.getInstance(GeocacheListAdapter.class);
    }

    public DistanceUpdater(GeocacheListAdapter geocacheListAdapter) {
        mGeocacheListAdapter = geocacheListAdapter;
    }

    @Override
    public void refresh() {
        Log.d("GeoBeagle", "DistanceUpdater: notifyDataSetChanged");
        mGeocacheListAdapter.notifyDataSetChanged();
    }
}
