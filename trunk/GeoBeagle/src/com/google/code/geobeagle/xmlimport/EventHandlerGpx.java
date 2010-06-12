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

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.inject.Inject;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

public class EventHandlerGpx implements EventHandler {
    static final String LOG_TEXT = "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:text";
    static final String LONG_DESCRIPTION = "/gpx/wpt/groundspeak:cache/groundspeak:long_description";
    static final String XPATH_CACHE_CONTAINER = "/gpx/wpt/groundspeak:cache/groundspeak:container";
    static final String XPATH_CACHE_DIFFICULTY = "/gpx/wpt/groundspeak:cache/groundspeak:difficulty";
    static final String XPATH_CACHE_TERRAIN = "/gpx/wpt/groundspeak:cache/groundspeak:terrain";
    static final String XPATH_CACHE_TYPE = "/gpx/wpt/groundspeak:cache/groundspeak:type";
    static final String XPATH_GEOCACHE_CONTAINER = "/gpx/wpt/geocache/container";
    static final String XPATH_GEOCACHE_DIFFICULTY = "/gpx/wpt/geocache/difficulty";
    static final String XPATH_GEOCACHE_TERRAIN = "/gpx/wpt/geocache/terrain";
    static final String XPATH_GEOCACHE_TYPE = "/gpx/wpt/geocache/type";
    static final String XPATH_GEOCACHEHINT = "/gpx/wpt/geocache/hints";
    static final String XPATH_GEOCACHELOGDATE = "/gpx/wpt/geocache/logs/log/time";
    static final String XPATH_GEOCACHENAME = "/gpx/wpt/geocache/name";
    static final String XPATH_GPXTIME = "/gpx/time";
    static final String XPATH_TERRACACHINGGPXTIME = "/gpx/metadata/time";
    static final String XPATH_GROUNDSPEAKNAME = "/gpx/wpt/groundspeak:cache/groundspeak:name";
    static final String XPATH_HINT = "/gpx/wpt/groundspeak:cache/groundspeak:encoded_hints";
    static final String SHORT_DESCRIPTION = "/gpx/wpt/groundspeak:cache/groundspeak:short_description";
    static final String XPATH_CACHE = "/gpx/wpt/groundspeak:cache";
    static final String XPATH_LOGDATE = "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:date";
    static final String[] XPATH_PLAINLINES = {
            "/gpx/wpt/cmt", "/gpx/wpt/desc", "/gpx/wpt/groundspeak:cache/groundspeak:type",
            "/gpx/wpt/groundspeak:cache/groundspeak:container", SHORT_DESCRIPTION,
            LONG_DESCRIPTION,
            "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:type",
            "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:finder",
            LOG_TEXT,
            /* here are the geocaching.com.au entries */
            "/gpx/wpt/geocache/owner", "/gpx/wpt/geocache/type", "/gpx/wpt/geocache/summary",
            "/gpx/wpt/geocache/description", "/gpx/wpt/geocache/logs/log/geocacher",
            "/gpx/wpt/geocache/logs/log/type", "/gpx/wpt/geocache/logs/log/text"

    };
    static final String XPATH_LAST_MODIFIED = "/gpx/wpt/bcaching:cache/bcaching:lastModified";
    static final String XPATH_SYM = "/gpx/wpt/sym";
    static final String XPATH_WPT = "/gpx/wpt";
    static final String XPATH_WPTDESC = "/gpx/wpt/desc";
    static final String XPATH_WPTNAME = "/gpx/wpt/name";
    static final String XPATH_WAYPOINT_TYPE = "/gpx/wpt/type";
    private final CachePersisterFacade mCachePersisterFacade;

    @Inject
    public EventHandlerGpx(CachePersisterFacade cachePersisterFacade) {
        mCachePersisterFacade = cachePersisterFacade;
    }

    @Override
    public void endTag(String name, String previousFullPath) throws IOException {
        if (previousFullPath.equals(XPATH_WPT)) {
            mCachePersisterFacade.endCache(Source.GPX);
        }
    }

    @Override
    public void startTag(String name, String fullPath, XmlPullParserWrapper xmlPullParser)
            throws IOException {
        HashMap<String, String> attributes = new HashMap<String, String>();
        
        int attributeCount = xmlPullParser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            attributes.put(xmlPullParser.getAttributeName(i), xmlPullParser.getAttributeValue(i));
        }

        Log.d("GeoBeagle", "startTag: name/fullpath: " + name + "/" + fullPath);

        if (fullPath.equals(XPATH_WPT)) {
            Log.d("GeoBeagle", "starting cache");
            mCachePersisterFacade.startCache();
            mCachePersisterFacade.wpt(attributes.get("lat"), attributes.get("lon"));
        } else if (fullPath.equals(EventHandlerGpx.XPATH_CACHE)) {
            mCachePersisterFacade.available(attributes.get("available"));
            mCachePersisterFacade.archived(attributes.get("archived"));
        }
    }

    public boolean text(String fullPath, String text) throws IOException {
        String trimmedText = text.trim();
        if (trimmedText.length() == 0) {
            return true;
        }
        Log.d("GeoBeagle", "fullPath " + fullPath + ", text " + text);
        if (fullPath.equals(XPATH_WPTNAME)) {
            mCachePersisterFacade.wptName(trimmedText);
        } else if (fullPath.equals(XPATH_WPTDESC)) {
            mCachePersisterFacade.wptDesc(trimmedText);
        } else if (fullPath.equals(XPATH_GPXTIME) || fullPath.equals(XPATH_TERRACACHINGGPXTIME)) {
            return mCachePersisterFacade.gpxTime(trimmedText);
        } else if (fullPath.equals(XPATH_GROUNDSPEAKNAME) || fullPath.equals(XPATH_GEOCACHENAME)) {
            mCachePersisterFacade.groundspeakName(trimmedText);
        } else if (fullPath.equals(XPATH_LOGDATE) || fullPath.equals(XPATH_GEOCACHELOGDATE)) {
            mCachePersisterFacade.logDate(trimmedText);
        } else if (fullPath.equals(XPATH_SYM)) {
            mCachePersisterFacade.symbol(trimmedText);
        } else if (fullPath.equals(XPATH_HINT) || fullPath.equals(XPATH_GEOCACHEHINT)) {
            if (!trimmedText.equals("")) {
                mCachePersisterFacade.hint(trimmedText);
            }
        } else if (fullPath.equals(XPATH_CACHE_TYPE) || fullPath.equals(XPATH_GEOCACHE_TYPE)
                || fullPath.equals(XPATH_WAYPOINT_TYPE)) {
            //Log.d("GeoBeagle", "Setting cache type " + text);
            mCachePersisterFacade.cacheType(trimmedText);
        } else if (fullPath.equals(XPATH_CACHE_DIFFICULTY)
                || fullPath.equals(XPATH_GEOCACHE_DIFFICULTY)) {
            mCachePersisterFacade.difficulty(trimmedText);
        } else if (fullPath.equals(XPATH_CACHE_TERRAIN) || fullPath.equals(XPATH_GEOCACHE_TERRAIN)) {
            mCachePersisterFacade.terrain(trimmedText);
        } else if (fullPath.equals(XPATH_CACHE_CONTAINER)
                || fullPath.equals(XPATH_GEOCACHE_CONTAINER)) {
            mCachePersisterFacade.container(trimmedText);
        } else if (fullPath.equals(XPATH_LAST_MODIFIED)) {
            mCachePersisterFacade.lastModified(trimmedText);
        } 
        for (String writeLineMatch : XPATH_PLAINLINES) {
            if (fullPath.equals(writeLineMatch)) {
                mCachePersisterFacade.line(trimmedText);
                return true;
            }
        }

        return true;
    }

    @Override
    public void open(String filename) {
    }
}
