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

package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.inject.Inject;
import com.google.inject.Provider;

import roboguice.util.RoboThread;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImportThread extends RoboThread {

    private GpxSyncer gpxSyncer;
    private final GpxSyncerFactory gpxSyncerFactory;
    private final Provider<ImportBCachingWorker> importBCachingWorkerProvider;
    private ImportBCachingWorker importBCachingWorker;
    private boolean isAlive;
    private final ErrorDisplayer errorDisplayer;
    private final UpdateFlag updateFlag;

    @Inject
    ImportThread(GpxSyncerFactory gpxSyncerFactory,
            Provider<ImportBCachingWorker> importBCachingWorkerProvider,
            ErrorDisplayer errorDisplayer,
            UpdateFlag updateFlag) {
        this.gpxSyncerFactory = gpxSyncerFactory;
        this.importBCachingWorkerProvider = importBCachingWorkerProvider;
        this.errorDisplayer = errorDisplayer;
        this.updateFlag = updateFlag;
    }

    @Override
    public void run() {
        isAlive = true;
        updateFlag.setUpdatesEnabled(false);
        try {
            gpxSyncer.sync();
            importBCachingWorker.sync();
        } catch (final FileNotFoundException e) {
            errorDisplayer.displayError(R.string.error_opening_file, e.getMessage());
            return;
        } catch (IOException e) {
            errorDisplayer.displayError(R.string.error_reading_file, e.getMessage());
            return;
        } catch (ImportException e) {
            errorDisplayer.displayError(e.getError(), e.getPath());
            return;
        } catch (BCachingException e) {
            errorDisplayer.displayError(R.string.problem_importing_from_bcaching,
                    e.getLocalizedMessage());
        } catch (CancelException e) {
            // Toast can't be displayed in this thread; it must be displayed in
            // main UI thread.
            return;
        } finally {
            Log.d("GeoBeagle", "<<< Syncing");
            isAlive = false;
            updateFlag.setUpdatesEnabled(true);
        }
    }

    public boolean isAliveHack() {
        Log.d("GeoBeagle", "ImportThread:isAliveHack(): " + isAlive);
        return isAlive;
    }

    public void init() {
        gpxSyncer = gpxSyncerFactory.create();
        importBCachingWorker = importBCachingWorkerProvider.get();
    }
}
