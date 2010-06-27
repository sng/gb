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

package com.google.code.geobeagle.activity.cachelist.actions.menu;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActionBase;
import com.google.code.geobeagle.activity.cachelist.GpxImporterFactory;
import com.google.code.geobeagle.activity.cachelist.NullAbortable;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.GpxWriter;
import com.google.code.geobeagle.xmlimport.GpxImporter;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.util.Log;

public class MenuActionSyncGpx extends MenuActionBase {
    private Abortable mSdcardImportAbortable;
    private final CacheListRefresh mCacheListRefresh;
    private final GpxImporterFactory mGpxImporterFactory;
    private final Provider<CacheWriter> mCacheWriterProvider;
    private final Provider<GpxWriter> mGpxWriterProvider;
    private final Provider<ImportBCachingWorker> mImportBCachingWorkerProvider;
    private Abortable mBCachingWorkerAborter;
    private Abortable mNullAbortable;

    @Inject
    public MenuActionSyncGpx(Injector injector,
            NullAbortable nullAbortable, CacheListRefresh cacheListRefresh,
            Provider<CacheWriter> cacheWriterProvider,
            Provider<GpxWriter> gpxWriterProvider) {
        super(R.string.menu_sync);
        mNullAbortable = nullAbortable;
        mSdcardImportAbortable = nullAbortable;
        mBCachingWorkerAborter = nullAbortable;
        mCacheListRefresh = cacheListRefresh;
        mGpxImporterFactory = null;
        mCacheWriterProvider = cacheWriterProvider;
        mGpxWriterProvider = gpxWriterProvider;
        mImportBCachingWorkerProvider = null;
    }

    public void abort() {
        Log.d("GeoBeagle", "MenuActionSyncGpx aborting");
        mSdcardImportAbortable.abort();
        mBCachingWorkerAborter.abort();
        mSdcardImportAbortable = mNullAbortable;
        mBCachingWorkerAborter = mNullAbortable;
    }

    public void act() {
        final GpxImporter gpxImporter = mGpxImporterFactory.create(mCacheWriterProvider.get(),
                mGpxWriterProvider.get());
        mSdcardImportAbortable = gpxImporter;
        mBCachingWorkerAborter = mImportBCachingWorkerProvider.get();
        gpxImporter.importGpxs(mCacheListRefresh);
    }
}
