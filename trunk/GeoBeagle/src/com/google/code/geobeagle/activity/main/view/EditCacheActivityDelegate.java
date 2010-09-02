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
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.Util;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditCacheActivityDelegate {
    public static class CancelButtonOnClickListener implements OnClickListener {
        private final Activity mActivity;

        @Inject
        public CancelButtonOnClickListener(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onClick(View v) {
            // TODO: replace magic number.
            mActivity.setResult(-1, null);
            mActivity.finish();
        }
    }

    public static class EditCache {
        private final GeocacheFactory mGeocacheFactory;
        private final EditText mId;
        private final EditText mLatitude;
        private final EditText mLongitude;
        private final EditText mName;
        private Geocache mOriginalGeocache;

        public EditCache(GeocacheFactory geocacheFactory, EditText id, EditText name,
                EditText latitude, EditText longitude) {
            mGeocacheFactory = geocacheFactory;
            mId = id;
            mName = name;
            mLatitude = latitude;
            mLongitude = longitude;
        }

        Geocache get() {
            return mGeocacheFactory.create(mId.getText(), mName.getText(), Util
                    .parseCoordinate(mLatitude.getText()), Util.parseCoordinate(mLongitude
                    .getText()), mOriginalGeocache.getSourceType(), mOriginalGeocache
                    .getSourceName(), mOriginalGeocache.getCacheType(), mOriginalGeocache
                    .getDifficulty(), mOriginalGeocache.getTerrain(), mOriginalGeocache
                    .getContainer(), mOriginalGeocache.getAvailable(), mOriginalGeocache
                    .getArchived());
        }

        void set(Geocache geocache) {
            mOriginalGeocache = geocache;
            mId.setText(geocache.getId());
            mName.setText(geocache.getName());
            mLatitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLatitude()));
            mLongitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLongitude()));

            mLatitude.requestFocus();
        }
    }

    public static class SetButtonOnClickListener implements OnClickListener {
        private final Activity mActivity;
        private final EditCache mGeocacheView;
        private final LocationSaver mLocationSaver;

        public SetButtonOnClickListener(Activity activity, EditCache editCache,
                LocationSaver locationSaver) {
            mActivity = activity;
            mGeocacheView = editCache;
            mLocationSaver = locationSaver;
        }

        @Override
        public void onClick(View v) {
            final Geocache geocache = mGeocacheView.get();
            mLocationSaver.saveLocation(geocache);
            final Intent i = new Intent();
            i.setAction(GeocacheListController.SELECT_CACHE);
            i.putExtra("geocache", geocache);
            mActivity.setResult(0, i);
            mActivity.finish();
        }
    }

    private final CancelButtonOnClickListener cancelButtonOnClickListener;
    private final Provider<DbFrontend> dbFrontendProvider;
    private final GeocacheFactory geocacheFactory;
    private final LocationSaver locationSaver;
    private final Activity parent;

    @Inject
    public EditCacheActivityDelegate(Activity parent,
            CancelButtonOnClickListener cancelButtonOnClickListener,
            GeocacheFactory geocacheFactory,
            LocationSaver locationSaver,
            Provider<DbFrontend> dbFrontendProvider) {
        this.parent = parent;
        this.cancelButtonOnClickListener = cancelButtonOnClickListener;
        this.geocacheFactory = geocacheFactory;
        this.locationSaver = locationSaver;
        this.dbFrontendProvider = dbFrontendProvider;
    }

    public void onCreate() {
        parent.setContentView(R.layout.cache_edit);
    }

    public void onPause() {
        dbFrontendProvider.get().closeDatabase();
    }

    public void onResume() {
        final Intent intent = parent.getIntent();
        final Geocache geocache = intent.<Geocache> getParcelableExtra("geocache");
        final EditCache editCache = new EditCache(geocacheFactory, (EditText)parent
                .findViewById(R.id.edit_id), (EditText)parent.findViewById(R.id.edit_name),
                (EditText)parent.findViewById(R.id.edit_latitude), (EditText)parent
                        .findViewById(R.id.edit_longitude));

        editCache.set(geocache);
        final SetButtonOnClickListener setButtonOnClickListener = new SetButtonOnClickListener(
                parent, editCache, locationSaver);

        ((Button)parent.findViewById(R.id.edit_set)).setOnClickListener(setButtonOnClickListener);
        ((Button)parent.findViewById(R.id.edit_cancel))
                .setOnClickListener(cancelButtonOnClickListener);
    }
}
