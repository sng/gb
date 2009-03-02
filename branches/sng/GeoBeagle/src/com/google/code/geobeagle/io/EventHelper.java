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

import com.google.code.geobeagle.io.GpxLoader.Cache;
import com.google.code.geobeagle.io.GpxToCache.XmlPullParserWrapper;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

public class EventHelper {
    public static class XmlPathBuilder {
        private String mPath = "";
    
        public void endTag(String currentTag) {
            mPath = mPath.substring(0, mPath.length() - (currentTag.length() + 1));
        }
    
        public String getPath() {
            return mPath;
        }
    
        public void startTag(String mCurrentTag) {
            mPath += "/" + mCurrentTag;
        }
    }

    public static EventHelper create(XmlPullParserWrapper xmlPullParser) {
        final GpxEventHandler gpxEventHandler = GpxEventHandler.create();
        final XmlPathBuilder xmlPathBuilder = new XmlPathBuilder();
        return new EventHelper(xmlPathBuilder, gpxEventHandler, xmlPullParser);
    }

    private final GpxEventHandler mGpxEventHandler;
    private final XmlPathBuilder mXmlPathBuilder;
    private final XmlPullParserWrapper mXmlPullParser;

    public EventHelper(XmlPathBuilder xmlPathBuilder, GpxEventHandler gpxEventHandler,
            XmlPullParserWrapper xmlPullParser) {
        mXmlPathBuilder = xmlPathBuilder;
        mGpxEventHandler = gpxEventHandler;
        mXmlPullParser = xmlPullParser;
    }

    public Cache handleEvent(int eventType) throws IOException {
        Cache cache = null;
        switch (eventType) {
            case XmlPullParser.START_TAG:
                mXmlPathBuilder.startTag(mXmlPullParser.getName());
                mGpxEventHandler.startTag(mXmlPathBuilder.getPath(), mXmlPullParser);
                break;
            case XmlPullParser.END_TAG:
                cache = mGpxEventHandler.endTag(mXmlPathBuilder.getPath());
                mXmlPathBuilder.endTag(mXmlPullParser.getName());
                break;
            case XmlPullParser.TEXT:
                mGpxEventHandler.text(mXmlPathBuilder.getPath(), mXmlPullParser.getText());
                break;
        }
        return cache;
    }
}