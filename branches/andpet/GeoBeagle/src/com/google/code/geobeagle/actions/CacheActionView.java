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

package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.GeoBeagle;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

public class CacheActionView  extends ActionStaticLabel implements CacheAction {
    private final Context mContext;

    public CacheActionView(Context context, Resources resources) {
        super(resources, R.string.menu_view_geocache);
        mContext = context;
    }

    @Override
    public void act(Geocache cache) {
        final Intent intent = new Intent(mContext, GeoBeagle.class);
        intent.setAction(GeocacheListController.SELECT_CACHE);
        intent.putExtra("geocacheId", cache.getId());
        mContext.startActivity(intent);
    }
}
