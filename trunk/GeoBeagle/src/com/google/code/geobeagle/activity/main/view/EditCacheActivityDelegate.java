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

package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

public class EditCacheActivityDelegate {
    private final CancelButtonOnClickListener mCancelButtonOnClickListener;
    private final GeocacheFactory mGeocacheFactory;
    private final Activity mParent;
    private final LocationSaver mLocationSaver;
    private final Provider<DbFrontend> mDbFrontendProvider;

    @Inject
    public EditCacheActivityDelegate(Activity parent,
            CancelButtonOnClickListener cancelButtonOnClickListener,
            GeocacheFactory geocacheFactory,
            LocationSaver locationSaver,
            Provider<DbFrontend> dbFrontendProvider) {
        mParent = parent;
        mCancelButtonOnClickListener = cancelButtonOnClickListener;
        mGeocacheFactory = geocacheFactory;
        mLocationSaver = locationSaver;
        mDbFrontendProvider = dbFrontendProvider;
    }

    public void onCreate() {
        mParent.setContentView(R.layout.cache_edit);
    }

    public void onResume() {
        final Intent intent = mParent.getIntent();
        final Geocache geocache = intent.<Geocache> getParcelableExtra("geocache");
        final EditCache editCache = new EditCache(mGeocacheFactory, (EditText)mParent
                .findViewById(R.id.edit_id), (EditText)mParent.findViewById(R.id.edit_name),
                (EditText)mParent.findViewById(R.id.edit_latitude), (EditText)mParent
                        .findViewById(R.id.edit_longitude));

        editCache.set(geocache);
        final SetButtonOnClickListener setButtonOnClickListener = new SetButtonOnClickListener(
                mParent, editCache, mLocationSaver);

        ((Button)mParent.findViewById(R.id.edit_set)).setOnClickListener(setButtonOnClickListener);
        ((Button)mParent.findViewById(R.id.edit_cancel))
                .setOnClickListener(mCancelButtonOnClickListener);
    }

    public void onPause() {
        mDbFrontendProvider.get().closeDatabase();
    }
}
