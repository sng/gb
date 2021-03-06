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

package com.google.code.geobeagle.cachedetails.reader;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.inject.Provider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;

import java.io.IOException;
import java.io.Reader;

public class DetailsReader {
    private final Activity mActivity;
    private final EventHelper mEventHelper;
    private final String mPath;
    private final Reader mReader;
    private final StringWriterWrapper mStringWriterWrapper;
    private XmlPullParser mXmlPullParserWrapper;
    private final Provider<XmlPullParser> mXmlPullParserProvider;

    public DetailsReader(Activity activity,
            Reader fileReader,
            String path,
            EventHelper eventHelper,
            StringWriterWrapper stringWriterWrapper,
            Provider<XmlPullParser> xmlPullParserProvider) {
        mActivity = activity;
        mPath = path;
        mEventHelper = eventHelper;
        mReader = fileReader;
        mStringWriterWrapper = stringWriterWrapper;
        mXmlPullParserProvider = xmlPullParserProvider;
    }

    public String read() {
        try {
            mEventHelper.open(mPath);
            XmlPullParser newPullParser = mXmlPullParserProvider.get();
            newPullParser.setInput(mReader);
            mXmlPullParserWrapper = newPullParser;
            int eventType;
            for (eventType = mXmlPullParserWrapper.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = mXmlPullParserWrapper
                    .next()) {
                mEventHelper.handleEvent(eventType, mXmlPullParserWrapper);
            }

            // Pick up END_DOCUMENT event as well.
            mEventHelper.handleEvent(eventType, newPullParser);

            return mStringWriterWrapper.getString();
        } catch (XmlPullParserException e) {
            return mActivity.getString(R.string.error_reading_details_file, mPath);
        } catch (IOException e) {
            return mActivity.getString(R.string.error_reading_details_file, mPath);
        }
    }
}
