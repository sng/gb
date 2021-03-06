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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.actions.ActionStaticLabel;
import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.activity.cachelist.GpxImporterFactory;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxImporter;

import android.content.res.Resources;

public class MenuActionSyncGpx extends ActionStaticLabel implements MenuAction {
    private Abortable mAbortable;
    private final CacheListAdapter mCacheList;
    private final GpxImporterFactory mGpxImporterFactory;
    private final DbFrontend mDbFrontend;

    public MenuActionSyncGpx(Abortable abortable, CacheListAdapter cacheList,
            GpxImporterFactory gpxImporterFactory, DbFrontend dbFrontend,
            Resources resources) {
        super(resources, R.string.menu_sync);
        mAbortable = abortable;
        mCacheList = cacheList;
        mGpxImporterFactory = gpxImporterFactory;
        mDbFrontend = dbFrontend;
    }

    public void abort() {
        mAbortable.abort();
    }

    @Override
    public void act() {
        //Only the most recent batch of caches is shown as 'new'
        mDbFrontend.clearTagForAllCaches(Tags.NEW);
        final GpxImporter gpxImporter = mGpxImporterFactory.create(mDbFrontend.getCacheWriter());
        mAbortable = gpxImporter;
        gpxImporter.importGpxs(mCacheList);
    }
}
