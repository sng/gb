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

package com.google.code.geobeagle.activity.map;

import com.google.android.maps.MapView;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActionBase;
import com.google.code.geobeagle.actions.MenuActions;

import android.view.Menu;
import android.view.MenuItem;

public class GeoMapActivityDelegate {
    public static class MenuActionToggleSatellite extends MenuActionBase {
        private final MapView mMapView;

        public MenuActionToggleSatellite(MapView mapView) {
            super(R.string.menu_toggle_satellite);
            mMapView = mapView;
        }

        @Override
        public void act() {
            mMapView.setSatellite(!mMapView.isSatellite());
        }
    }

    private final GeoMapView mMapView;
    private final MenuActions mMenuActions;

    public GeoMapActivityDelegate(GeoMapView mapView, MenuActions menuActions) {
        mMapView = mapView;
        mMenuActions = menuActions;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenuActions.onCreateOptionsMenu(menu);
    }

    public boolean onMenuOpened(Menu menu) {
        menu.findItem(R.string.menu_toggle_satellite).setTitle(
                mMapView.isSatellite() ? R.string.map_view : R.string.satellite_view);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuActions.act(item.getItemId());
    }

}
