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
import com.google.code.geobeagle.database.GpxWriter;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;

import android.os.PowerManager.WakeLock;

import java.text.SimpleDateFormat;

public class GpxLoaderDI {
    public static GpxLoader create(CachePersisterFacade cachePersisterFacade,
            XmlPullParserWrapper xmlPullParserWrapper, Aborter aborter,
            ErrorDisplayer errorDisplayer, WakeLock wakeLock, GpxWriter gpxWriter) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        FileAlreadyLoadedChecker fileAlreadyLoadedChecker = new FileAlreadyLoadedChecker(gpxWriter,
                simpleDateFormat);
        final GpxToCache gpxToCache = new GpxToCache(xmlPullParserWrapper, aborter,
                fileAlreadyLoadedChecker);
        return new GpxLoader(cachePersisterFacade, errorDisplayer, gpxToCache, wakeLock);
    }
}
