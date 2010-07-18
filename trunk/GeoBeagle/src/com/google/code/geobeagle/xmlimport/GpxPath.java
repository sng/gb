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
    XPATH_WPTNAME("/gpx/wpt/name", PathType.WPT), XPATH_WPTDESC("/gpx/wpt/desc", PathType.DESC);

    private final String path;
    private final PathType pathType;

    GpxPath(String path, PathType pathType) {
        this.path = path;
        this.pathType = pathType;
    }

    private static final Map<String, GpxPath> stringToEnum = new HashMap<String, GpxPath>();
    static {
        for (GpxPath gpxPath : values())
            stringToEnum.put(gpxPath.getPath(), gpxPath);
    }

    // Returns Operation for string, or null if string is invalid
    public static GpxPath fromString(String symbol) {
        return stringToEnum.get(symbol);
    }

    public void text(String text, ICachePersisterFacade cachePersisterFacade) throws IOException {
        pathType.text(text, cachePersisterFacade);
    }

    public String getPath() {
        return path;
    }

    private enum PathType {
        WPT {
            @Override
            public void text(String text, ICachePersisterFacade cachePersisterFacade)
                    throws IOException {
                cachePersisterFacade.wptName(text);
            }
        },
        DESC {
            @Override
            public void text(String text, ICachePersisterFacade cachePersisterFacade)
                    throws IOException {
                cachePersisterFacade.wptDesc(text);
            }
        };
        abstract void text(String text, ICachePersisterFacade cachePersisterFacade)
                throws IOException;
    }
}
