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
import com.google.code.geobeagle.activity.cachelist.presenter.UpdateFilterMessages;
import com.google.code.geobeagle.activity.preferences.EditPreferences;
import com.google.inject.Inject;

import roboguice.util.RoboThread;

import android.content.SharedPreferences;

public class UpdateFilterWorker extends RoboThread {
    private final DbFrontend dbFrontEnd;
    private final UpdateFlag updateFlag;
    private final UpdateFilterHandler updateFilterHandler;
    private final TagReader tagReader;
    private final FilterCleanliness filterCleanliness;
    private final SharedPreferences sharedPreferences;

    @Inject
    public UpdateFilterWorker(SharedPreferences sharedPreferences,
            DbFrontend dbFrontEnd,
            UpdateFlag updateFlag,
            UpdateFilterHandler updateFilterHandler,
            TagReader tagReader,
            FilterCleanliness filterCleanliness) {
        this.dbFrontEnd = dbFrontEnd;
        this.updateFlag = updateFlag;
        this.updateFilterHandler = updateFilterHandler;
        this.tagReader = tagReader;
        this.filterCleanliness = filterCleanliness;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void run() {
        boolean showFoundCaches = sharedPreferences.getBoolean(EditPreferences.SHOW_FOUND_CACHES,
                false);
        ISQLiteDatabase database = dbFrontEnd.getDatabase();
        database.execSQL("UPDATE CACHES SET Visible = 1");
        if (showFoundCaches) {
            updateFilterHandler.sendMessage(updateFilterHandler.obtainMessage(
                    UpdateFilterMessages.DISMISS_CLEAR_FILTER_PROGRESS.ordinal(), 0, 0));
            filterCleanliness.markDirty(false);
            return;
        }

        FoundCaches foundCaches = null;
        try {
            foundCaches = tagReader.getFoundCaches();
            updateFilterHandler.sendMessage(updateFilterHandler.obtainMessage(
                    UpdateFilterMessages.SHOW_APPLY_FILTER_PROGRESS.ordinal(),
                    foundCaches.getCount(), 0));

            for (String cache : foundCaches.getCaches()) {
                updateFilterHandler.sendMessage(updateFilterHandler.obtainMessage(
                        UpdateFilterMessages.INCREMENT_APPLY_FILTER_PROGRESS.ordinal(), 0, 0));
                database.execSQL("UPDATE CACHES SET Visible = 0 WHERE ID = ?", cache);
            }
        } finally {
            if (foundCaches != null)
                foundCaches.close();
        }
        filterCleanliness.markDirty(false);
        updateFlag.setUpdatesEnabled(true);
        updateFilterHandler.sendMessage(updateFilterHandler.obtainMessage(
                UpdateFilterMessages.DISMISS_APPLY_FILTER_PROGRESS.ordinal(), 0, 0));
    }
}
