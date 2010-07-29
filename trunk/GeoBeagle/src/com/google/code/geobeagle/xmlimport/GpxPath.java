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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum GpxPath {
    XPATH_AU_DESCRIPTION("/gpx/wpt/geocache/description", PathType.LINE),
    XPATH_AU_GEOCACHER("/gpx/wpt/geocache/logs/log/geocacher", PathType.LINE),
    XPATH_AU_LOGTEXT("/gpx/wpt/geocache/logs/log/text", PathType.LINE),
    XPATH_AU_LOGTYPE("/gpx/wpt/geocache/logs/log/type", PathType.LINE),
    XPATH_AU_OWNER("/gpx/wpt/geocache/owner", PathType.LINE),
    XPATH_AU_SUMMARY("/gpx/wpt/geocache/summary", PathType.LINE),
    XPATH_CACHE_CONTAINER("/gpx/wpt/groundspeak:cache/groundspeak:container", PathType.CONTAINER),
    XPATH_CACHE_DIFFICULTY("/gpx/wpt/groundspeak:cache/groundspeak:difficulty", PathType.DIFFICULTY),
    XPATH_CACHE_TERRAIN("/gpx/wpt/groundspeak:cache/groundspeak:terrain", PathType.TERRAIN),
    XPATH_EXT_LONGDESC("/gpx/wpt/extensions/cache/long_description", PathType.LONG_DESCRIPTION),
    XPATH_EXT_SHORTDESC("/gpx/wpt/extensions/cache/short_description", PathType.SHORT_DESCRIPTION),
    XPATH_GEOCACHE_CONTAINER("/gpx/wpt/geocache/container", PathType.CONTAINER),
    XPATH_GEOCACHE_DIFFICULTY("/gpx/wpt/geocache/difficulty", PathType.DIFFICULTY),
    XPATH_GEOCACHE_EXT_DIFFICULTY("/gpx/wpt/extensions/cache/difficulty", PathType.DIFFICULTY),
    XPATH_GEOCACHE_EXT_TERRAIN("/gpx/wpt/extensions/cache/terrain", PathType.TERRAIN),
    XPATH_GEOCACHE_TERRAIN("/gpx/wpt/geocache/terrain", PathType.TERRAIN),
    XPATH_GEOCACHE_TYPE("/gpx/wpt/geocache/type", PathType.CACHE_TYPE),
    XPATH_GEOCACHEHINT("/gpx/wpt/geocache/hints", PathType.HINT),
    XPATH_GEOCACHELOGDATE("/gpx/wpt/geocache/logs/log/time", PathType.LOG_DATE),
    XPATH_GEOCACHENAME("/gpx/wpt/geocache/name", PathType.NAME),
    XPATH_GPXTIME("/gpx/time", PathType.GPX_TIME),
    XPATH_GROUNDSPEAKFINDER(
            "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:finder",
            PathType.LINE),
    XPATH_GROUNDSPEAKNAME("/gpx/wpt/groundspeak:cache/groundspeak:name", PathType.NAME),
    XPATH_HINT("/gpx/wpt/groundspeak:cache/groundspeak:encoded_hints", PathType.HINT),
    XPATH_LAST_MODIFIED("/gpx/wpt/bcaching:cache/bcaching:lastModified", PathType.LAST_MODIFIED),
    XPATH_LOGDATE("/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:date",
            PathType.LOG_DATE),
    GPX_LOGFINDER("/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:finder",
            PathType.LOG_TEXT),
    GPX_LOGTEXT("/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:text",
            PathType.LOG_TEXT),
    GPX_LOGTYPE("/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:type",
            PathType.LOG_TYPE),
    XPATH_LONGDESC("/gpx/wpt/groundspeak:cache/groundspeak:long_description",
            PathType.LONG_DESCRIPTION),
    GPX_OCNAME("/gpx/wpt/extensions/cache/name", PathType.NAME),
    GPX_OCOWNER("/gpx/wpt/extensions/cache/owner", PathType.PLACED_BY),
    GPX_OCLOGDATE("/gpx/wpt/extensions/cache/logs/log/date", PathType.LOG_DATE),
    GPX_OCLOGFINDER("/gpx/wpt/extensions/cache/logs/log/finder", PathType.LOG_TEXT),
    GPX_OCLOGTEXT("/gpx/wpt/extensions/cache/logs/log/text", PathType.LOG_TEXT),
    GPX_OCLOGTYPE("/gpx/wpt/extensions/cache/logs/log/type", PathType.LOG_TYPE),
    GPX_PLACEDBY("/gpx/wpt/groundspeak:cache/groundspeak:placed_by", PathType.PLACED_BY),
    GPX_SHORTDESC("/gpx/wpt/groundspeak:cache/groundspeak:short_description",
            PathType.SHORT_DESCRIPTION),
    GPX_SYM("/gpx/wpt/sym", PathType.SYMBOL),
    GPX_TERRACACHINGGPXTIME("/gpx/metadata/time", PathType.GPX_TIME),
    GPX_WAYPOINT_TYPE("/gpx/wpt/type", PathType.CACHE_TYPE),
    GPX_WPT("/gpx/wpt", PathType.WPT),
    GPX_WPT_COMMENT("/gpx/wpt/cmt", PathType.LINE),
    GPX_WPTDESC("/gpx/wpt/desc", PathType.DESC),
    GPX_WPTNAME("/gpx/wpt/name", PathType.WPT_NAME),
    GPX_WPTTIME("/gpx/wpt/time", PathType.WPT_TIME),
    LOC_COORD("/loc/waypoint/coord", PathType.LOC_COORD),
    LOC_WPT("/loc/waypoint", PathType.LOC_WPT),
    LOC_WPTNAME("/loc/waypoint/name", PathType.LOC_WPTNAME);

    private static final Map<String, GpxPath> stringToEnum = new HashMap<String, GpxPath>();

    static {
        for (GpxPath gpxPath : values())
            stringToEnum.put(gpxPath.getPath(), gpxPath);
    }

    public static GpxPath fromString(String symbol) {
        return stringToEnum.get(symbol);
    }

    private final String path;
    private final PathType pathType;

    GpxPath(String path, PathType pathType) {
        this.path = path;
        this.pathType = pathType;
    }

    public String getPath() {
        return path;
    }

    public void startTag(XmlPullParserWrapper xmlPullParser,
            ICachePersisterFacade cachePersisterFacade) throws IOException {
        pathType.startTag(xmlPullParser, cachePersisterFacade);
    }

    public boolean text(String text, ICachePersisterFacade cachePersisterFacade) throws IOException {
        return pathType.text(text, cachePersisterFacade);
    }
}
