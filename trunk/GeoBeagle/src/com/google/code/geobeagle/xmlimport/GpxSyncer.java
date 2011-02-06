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

import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilesAndZipFilesIter;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;

import android.util.Log;

import java.io.IOException;

public class GpxSyncer {

    private final FileDataVersionWriter fileDataVersionWriter;
    private final GpxAndZipFiles gpxAndZipFiles;
    private final GpxToCache gpxToCache;
    private boolean mHasFiles;
    private final MessageHandler messageHandler;
    private final OldCacheFilesCleaner oldCacheFilesCleaner;

    public GpxSyncer(GpxAndZipFiles gpxAndZipFiles,
            FileDataVersionWriter fileDataVersionWriter,
            MessageHandler messageHandlerInterface,
            OldCacheFilesCleaner oldCacheFilesCleaner,
            GpxToCache gpxToCache) {
        this.gpxAndZipFiles = gpxAndZipFiles;
        this.fileDataVersionWriter = fileDataVersionWriter;
        this.messageHandler = messageHandlerInterface;
        this.mHasFiles = false;
        this.oldCacheFilesCleaner = oldCacheFilesCleaner;
        this.gpxToCache = gpxToCache;
    }

    public boolean sync() throws IOException, ImportException, CancelException {
        try {
            GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = startImport();
            while (gpxFilesAndZipFilesIter.hasNext()) {
                processFile(gpxFilesAndZipFilesIter);
            }
            return endImport();
        } finally {
            Log.d("GeoBeagle", "<<< Syncing");
            messageHandler.loadComplete();
        }
    }

    private boolean endImport() throws IOException {
        fileDataVersionWriter.writeVersion();
        gpxToCache.end();
        return mHasFiles;
    }

    private void processFile(GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter) throws IOException,
            CancelException {
        IGpxReader gpxReader = gpxFilesAndZipFilesIter.next();
        String filename = gpxReader.getFilename();

        mHasFiles = true;
        gpxToCache.load(filename, gpxReader.open());
    }

    private GpxFilesAndZipFilesIter startImport() throws ImportException {
        oldCacheFilesCleaner.clean(messageHandler);
        gpxToCache.start();
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = gpxAndZipFiles.iterator();
        return gpxFilesAndZipFilesIter;
    }
}