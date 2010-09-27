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

package com.google.code.geobeagle.activity.main.intents;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.cacheloader.CacheLoaderException;
import com.google.code.geobeagle.cacheloader.CacheUrlLoader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.res.Resources;
import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    Log.class
})
public class GeocacheToCachePageTest extends GeoBeagleTest {
    @Test
    public void convertGpxShouldUseCacheUrlLoader() throws CacheLoaderException {
        CacheUrlLoader cacheUrlLoader = createMock(CacheUrlLoader.class);
        Geocache geocache = createMock(Geocache.class);
        Resources resources = createMock(Resources.class);

        expect(geocache.getSourceType()).andReturn(Source.GPX);
        expect(geocache.getId()).andReturn("GCFOO");
        expect(geocache.getSourceName()).andReturn("bcaching.com");
        expect(cacheUrlLoader.load("bcaching.com", "GCFOO")).andReturn("http://coord.info/GCFOO");
        replayAll();

        GeocacheToCachePage geocacheToCachePage = new GeocacheToCachePage(cacheUrlLoader, resources);
        assertEquals("http://coord.info/GCFOO", geocacheToCachePage.convert(geocache));
        verifyAll();
    }

    @Test
    public void convertNonGpxShouldUseStringRule() throws CacheLoaderException {
        CacheUrlLoader cacheUrlLoader = createMock(CacheUrlLoader.class);
        Geocache geocache = createMock(Geocache.class);
        Resources resources = createMock(Resources.class);

        expect(geocache.getSourceType()).andReturn(Source.WEB_URL);
        expect(geocache.getContentProvider()).andReturn(GeocacheFactory.Provider.GROUNDSPEAK);
        expect(resources.getStringArray(R.array.cache_page_url)).andReturn(new String[] {
                "", "http://coord.info/GC%1$s",
        });
        expect(geocache.getShortId()).andReturn("FOO");
        replayAll();

        GeocacheToCachePage geocacheToCachePage = new GeocacheToCachePage(cacheUrlLoader, resources);
        assertEquals("http://coord.info/GCFOO", geocacheToCachePage.convert(geocache));
        verifyAll();
    }
}
