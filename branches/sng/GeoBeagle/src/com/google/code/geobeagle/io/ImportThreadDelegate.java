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

package com.google.code.geobeagle.io;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.gpx.IGpxReader;
import com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilesIter;
import com.google.code.geobeagle.io.EventHelperDI.EventHelperFactory;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImportThreadDelegate {
    public static class ImportThreadHelper {
        private final ErrorDisplayer mErrorDisplayer;
        private final EventHandlers mEventHandlers;
        private final EventHelperFactory mEventHelperFactory;
        private final GpxLoader mGpxLoader;
        private boolean mHasFiles;
        private final MessageHandler mMessageHandler;

        public ImportThreadHelper(GpxLoader gpxLoader, MessageHandler messageHandler,
                EventHelperFactory eventHelperFactory, EventHandlers eventHandlers,
                ErrorDisplayer errorDisplayer) {
            mErrorDisplayer = errorDisplayer;
            mGpxLoader = gpxLoader;
            mMessageHandler = messageHandler;
            mEventHelperFactory = eventHelperFactory;
            mEventHandlers = eventHandlers;
            mHasFiles = false;
        }

        public void cleanup() {
            mMessageHandler.loadComplete();
        }

        public void end() {
            mGpxLoader.end();
            if (!mHasFiles)
                mErrorDisplayer.displayError(R.string.error_no_gpx_files);
        }

        public boolean processFile(IGpxReader gpxReader) throws FileNotFoundException,
                XmlPullParserException, IOException {
            String filename = gpxReader.getFilename();

            mHasFiles = true;
            mGpxLoader.open(filename, gpxReader.open());
            return mGpxLoader.load(mEventHelperFactory.create(mEventHandlers.get(filename)));
        }

        public void start() {
            mGpxLoader.start();
        }
    }

    private ErrorDisplayer mErrorDisplayer;
    private final GpxAndZipFiles mGpxAndZipFiles;
    private ImportThreadHelper mImportThreadHelper;

    public ImportThreadDelegate(GpxAndZipFiles gpxAndZipFiles,
            ImportThreadHelper importThreadHelper, ErrorDisplayer errorDisplayer) {
        mGpxAndZipFiles = gpxAndZipFiles;
        mImportThreadHelper = importThreadHelper;
        mErrorDisplayer = errorDisplayer;
    }

    public void run() {
        try {
            tryRun();
        } catch (final FileNotFoundException e) {
            mErrorDisplayer.displayError(R.string.error_opening_file, e.getMessage());
        } catch (Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        } finally {
            mImportThreadHelper.cleanup();
        }
    }

    protected void tryRun() throws FileNotFoundException, IOException, XmlPullParserException {
        GpxAndZipFilesIter gpxFileIter = mGpxAndZipFiles.iterator();
        if (gpxFileIter == null) {
            mErrorDisplayer.displayError(R.string.error_cant_read_sd);
            return;
        }

        mImportThreadHelper.start();

        while (gpxFileIter.hasNext()) {
            if (!mImportThreadHelper.processFile(gpxFileIter.next()))
                return;
        }
        mImportThreadHelper.end();
    }
}
