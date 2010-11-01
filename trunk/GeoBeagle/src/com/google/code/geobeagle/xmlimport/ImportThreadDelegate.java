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
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilesAndZipFilesIter;

import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImportThreadDelegate {

    private final ErrorDisplayer mErrorDisplayer;
    private final GpxAndZipFiles mGpxAndZipFiles;
    private final ImportThreadHelper mImportThreadHelper;
    private final FileDataVersionWriter mFileDataVersionWriter;
    private final FileDataVersionChecker mFileDataVersionChecker;
    private final DbFrontend mDbFrontend;
    private final BCachingStartTime mBCachingStartTime;
    private boolean mIsAlive;
    private final UpdateFlag mUpdateFlag;

    public ImportThreadDelegate(GpxAndZipFiles gpxAndZipFiles,
            ImportThreadHelper importThreadHelper, ErrorDisplayer errorDisplayer,
            FileDataVersionWriter fileDataVersionWriter,
            FileDataVersionChecker fileDataVersionChecker, DbFrontend dbFrontend,
            BCachingStartTime bcachingStartTime, UpdateFlag updateFlag) {
        mGpxAndZipFiles = gpxAndZipFiles;
        mImportThreadHelper = importThreadHelper;
        mErrorDisplayer = errorDisplayer;
        mFileDataVersionWriter = fileDataVersionWriter;
        mFileDataVersionChecker = fileDataVersionChecker;
        mBCachingStartTime = bcachingStartTime;
        mDbFrontend = dbFrontend;
        mUpdateFlag = updateFlag;
    }

    public synchronized void run() {
        mIsAlive = true;
        try {
            mUpdateFlag.setUpdatesEnabled(false);
            tryRun();
        } catch (final FileNotFoundException e) {
            mErrorDisplayer.displayError(R.string.error_opening_file, e.getMessage());
            return;
        } catch (IOException e) {
            mErrorDisplayer.displayError(R.string.error_reading_file, e.getMessage());
            return;
        } catch (XmlPullParserException e) {
            mErrorDisplayer.displayError(R.string.error_parsing_file, e.getMessage());
            return;
        } catch (ImportException e) {
            mErrorDisplayer.displayError(e.getError(), e.getPath());
            return;
        } catch (CancelException e) {
            return;
        } finally {
            mUpdateFlag.setUpdatesEnabled(true);
            mImportThreadHelper.cleanup();
            mIsAlive = false;
        }
        Log.d("GeoBeagle", "STARTING BCACHING IMPORT");
        mImportThreadHelper.startBCachingImport();
    }

    public synchronized boolean isAlive() {
        return mIsAlive;
    }

    protected void tryRun() throws IOException, XmlPullParserException, ImportException,
            CancelException {
        if (mFileDataVersionChecker.needsUpdating()) {
            mDbFrontend.forceUpdate();
            mBCachingStartTime.clearStartTime();
        }
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = mGpxAndZipFiles.iterator();

        mImportThreadHelper.start();
        while (gpxFilesAndZipFilesIter.hasNext()) {
            if (!mImportThreadHelper.processFile(gpxFilesAndZipFilesIter.next())) {
                throw new CancelException();
            }
        }
        mFileDataVersionWriter.writeVersion();
        mImportThreadHelper.end();
    }
}
