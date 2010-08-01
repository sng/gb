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

package com.google.code.geobeagle.cachedetails;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.xmlimport.CacheTagsToDetails;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.code.geobeagle.xmlimport.ICachePersisterFacade;
import com.google.code.geobeagle.xmlimport.XmlPullParserWrapper;
import com.google.inject.Inject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class CacheDetailsLoader {

    public static class DetailsOpener {
        private final Activity activity;
        private final EventHandlerGpx eventHandlerGpx;
        private final EventHelper eventHelper;
        private final FileDataVersionChecker fileDataVersionChecker;
        private final StringWriterWrapper stringWriterWrapper;
        private final XmlPullParserWrapper xmlPullParser;

        @Inject
        DetailsOpener(Activity activity,
                FileDataVersionChecker fileDataVersionChecker,
                EventHelper eventHelper,
                EventHandlerGpx eventHandlerGpx,
                XmlPullParserWrapper xmlPullParser,
                StringWriterWrapper stringWriterWrapper) {
            this.activity = activity;
            this.fileDataVersionChecker = fileDataVersionChecker;
            this.eventHelper = eventHelper;
            this.eventHandlerGpx = eventHandlerGpx;
            this.xmlPullParser = xmlPullParser;
            this.stringWriterWrapper = stringWriterWrapper;
        }

        DetailsReader open(File file) {
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                return new DetailsReaderError(activity, R.string.error_cant_read_sdroot, state);
            }
            final Reader fileReader;
            String absolutePath = file.getAbsolutePath();
            try {
                fileReader = new BufferedReader(new FileReader(absolutePath));
            } catch (FileNotFoundException e) {
                int error = fileDataVersionChecker.needsUpdating() ? R.string.error_details_file_version
                        : R.string.error_opening_details_file;
                return new DetailsReaderError(activity, error, e.getMessage());
            }
            return new DetailsReaderImpl(activity, fileReader, absolutePath, eventHelper,
                    eventHandlerGpx, xmlPullParser, stringWriterWrapper);
        }
    }

    interface DetailsReader {
        String read(ICachePersisterFacade cpf);
    }

    static class DetailsReaderError implements DetailsReader {
        private final Activity mActivity;
        private final int mError;
        private final String mPath;

        DetailsReaderError(Activity activity, int error, String path) {
            mActivity = activity;
            mPath = path;
            mError = error;
        }

        @Override
        public String read(ICachePersisterFacade cpf) {
            return mActivity.getString(mError, mPath);
        }
    }

    static class DetailsReaderImpl implements DetailsReader {
        private final Activity mActivity;
        private final EventHandlerGpx mEventHandlerGpx;
        private final EventHelper mEventHelper;
        private final String mPath;
        private final Reader mReader;
        private final StringWriterWrapper mStringWriterWrapper;
        private final XmlPullParserWrapper mXmlPullParserWrapper;

        DetailsReaderImpl(Activity activity,
                Reader fileReader,
                String path,
                EventHelper eventHelper,
                EventHandlerGpx eventHandlerGpx,
                XmlPullParserWrapper xmlPullParserWrapper,
                StringWriterWrapper stringWriterWrapper) {
            mActivity = activity;
            mPath = path;
            mEventHelper = eventHelper;
            mEventHandlerGpx = eventHandlerGpx;
            mXmlPullParserWrapper = xmlPullParserWrapper;
            mReader = fileReader;
            mStringWriterWrapper = stringWriterWrapper;
        }

        @Override
        public String read(ICachePersisterFacade cachePersisterFacade) {
            try {
                mEventHelper.open(mPath, mEventHandlerGpx);
                mXmlPullParserWrapper.open(mPath, mReader);
                int eventType;
                for (eventType = mXmlPullParserWrapper.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = mXmlPullParserWrapper
                        .next()) {
                    mEventHelper.handleEvent(eventType, mEventHandlerGpx, cachePersisterFacade);
                }

                // Pick up END_DOCUMENT event as well.
                mEventHelper.handleEvent(eventType, mEventHandlerGpx, cachePersisterFacade);

                return mStringWriterWrapper.getString();
            } catch (XmlPullParserException e) {
                return mActivity.getString(R.string.error_reading_details_file, mPath);
            } catch (IOException e) {
                return mActivity.getString(R.string.error_reading_details_file, mPath);
            }
        }
    }

    private final CacheTagsToDetails mCacheTagsToDetails;
    private final DetailsOpener mDetailsOpener;
    private final FilePathStrategy mFilePathStrategy;

    @Inject
    CacheDetailsLoader(DetailsOpener detailsOpener,
            FilePathStrategy filePathStrategy,
            CacheTagsToDetails cacheTagsToDetails) {
        mDetailsOpener = detailsOpener;
        mFilePathStrategy = filePathStrategy;
        mCacheTagsToDetails = cacheTagsToDetails;
    }

    public String load(CharSequence sourceName, CharSequence cacheId) {
        String path = mFilePathStrategy.getPath(sourceName, cacheId.toString(), "gpx");
        File file = new File(path);
        DetailsReader detailsReader = mDetailsOpener.open(file);
        return detailsReader.read(mCacheTagsToDetails);
    }
}
