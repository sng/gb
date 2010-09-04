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

package com.google.code.geobeagle.database;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.activity.cachelist.presenter.UpdateFilterHandler;
import com.google.code.geobeagle.activity.preferences.EditPreferences;
import com.google.inject.Inject;

import roboguice.util.RoboThread;

import android.content.SharedPreferences;

public class UpdateFilterWorker extends RoboThread {
    private final UpdateFlag updateFlag;
    private final UpdateFilterHandler updateFilterHandler;
    private final TagReader tagReader;
    private final FilterCleanliness filterCleanliness;
    private final SharedPreferences sharedPreferences;
    private final CacheVisibilityStore cacheVisibilityStore;

    @Inject
    public UpdateFilterWorker(SharedPreferences sharedPreferences,
            UpdateFlag updateFlag,
            UpdateFilterHandler updateFilterHandler,
            TagReader tagReader,
            FilterCleanliness filterCleanliness,
            CacheVisibilityStore cacheVisibilityStore) {
        this.updateFlag = updateFlag;
        this.updateFilterHandler = updateFilterHandler;
        this.tagReader = tagReader;
        this.filterCleanliness = filterCleanliness;
        this.sharedPreferences = sharedPreferences;
        this.cacheVisibilityStore = cacheVisibilityStore;
    }

    @Override
    public void run() {
        cacheVisibilityStore.setAllVisible();

        if (sharedPreferences.getBoolean(EditPreferences.SHOW_FOUND_CACHES, false)) {
            filterCleanliness.markDirty(false);
            updateFilterHandler.dismissClearFilterProgress();
            return;
        }

        FoundCaches foundCaches = null;
        try {
            foundCaches = tagReader.getFoundCaches();
            updateFilterHandler.showApplyFilterProgress(foundCaches.getCount());

            for (String cache : foundCaches.getCaches()) {
                updateFilterHandler.incrementApplyFilterProgress();
                cacheVisibilityStore.setInvisible(cache);
            }
        } finally {
            if (foundCaches != null)
                foundCaches.close();
        }
        filterCleanliness.markDirty(false);
        updateFilterHandler.dismissApplyFilterProgress();
    }
}
