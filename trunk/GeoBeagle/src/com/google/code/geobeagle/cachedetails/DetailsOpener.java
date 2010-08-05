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
import com.google.code.geobeagle.cachedetails.reader.DetailsReader;
import com.google.code.geobeagle.cachedetails.reader.DetailsReaderError;
import com.google.code.geobeagle.cachedetails.reader.DetailsReaderImpl;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.code.geobeagle.xmlimport.XmlPullParserWrapper;
import com.google.inject.Inject;

import android.app.Activity;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class DetailsOpener {
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
