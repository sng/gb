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

package com.google.code.geobeagle.activity.cachelist.actions;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.actions.ActionStaticLabel;
import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.database.DbFrontend;

import android.content.res.Resources;

public class MenuActionMyLocation extends ActionStaticLabel implements MenuAction {
    private final ErrorDisplayer mErrorDisplayer;
    private final DbFrontend mDbFrontend;
    private final GeocacheFactory mGeocacheFactory;
    private final GeoFixProvider mLocationControl;
    private final CacheAction mCacheActionEdit;

    public MenuActionMyLocation(ErrorDisplayer errorDisplayer,
            GeocacheFactory geocacheFactory,
            GeoFixProvider locationControl, DbFrontend dbFrontend,
            Resources resources, CacheAction cacheActionEdit) {
        super(resources, R.string.menu_add_my_location);
        mErrorDisplayer = errorDisplayer;
        mGeocacheFactory = geocacheFactory;
        mLocationControl = locationControl;
        mDbFrontend = dbFrontend;
        mCacheActionEdit = cacheActionEdit;
    }

    @Override
    public void act() {
        GeoFix location = mLocationControl.getLocation();
        if (location == null)
            return;

        long time = location.getTime();
        final Geocache newCache = mGeocacheFactory.create(String.format("ML%1$tk%1$tM%1$tS", time), String.format(
                "[%1$tk:%1$tM] My Location", time), location.getLatitude(),
                location.getLongitude(), Source.MY_LOCATION, "mylocation", CacheType.MY_LOCATION, 0, 0, 0);

        if (newCache == null) {
            mErrorDisplayer.displayError(R.string.current_location_null);
            return;
        }
        newCache.saveToDb(mDbFrontend);
        mCacheActionEdit.act(newCache);
        //Since the Edit activity will refresh the list, we don't need to do it
        //mListRefresher.forceRefresh();
    }
}
