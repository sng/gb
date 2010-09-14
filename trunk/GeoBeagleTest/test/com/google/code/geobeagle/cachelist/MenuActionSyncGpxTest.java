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

package com.google.code.geobeagle.cachelist;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.cachelist.GpxImporterFactory;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.GpxWriter;
import com.google.code.geobeagle.xmlimport.GpxImporter;
import com.google.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionSyncGpxTest extends GeoBeagleTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testAct() {
        GpxImporter gpxImporter = createMock(GpxImporter.class);
        CacheListRefresh cacheListRefresh = createMock(CacheListRefresh.class);
        GpxImporterFactory gpxImporterFactory = createMock(GpxImporterFactory.class);
        CacheWriter cacheWriter = createMock(CacheWriter.class);
        Provider<ImportBCachingWorker> importBCachingWorkerProvider = createMock(Provider.class);
        Provider<GpxWriter> gpxWriterProvider = createMock(Provider.class);
        Provider<CacheWriter> dbFrontendProvider = createMock(Provider.class);
        ImportBCachingWorker importBCachingWorker = createMock(ImportBCachingWorker.class);
        GpxWriter gpxWriter = createMock(GpxWriter.class);

        expect(gpxWriterProvider.get()).andReturn(gpxWriter);
        expect(dbFrontendProvider.get()).andReturn(cacheWriter);
        expect(importBCachingWorkerProvider.get()).andReturn(importBCachingWorker);
        expect(gpxImporterFactory.create()).andReturn(gpxImporter);
        gpxImporter.importGpxs();

        replayAll();
        final MenuActionSyncGpx menuActionSyncGpx = new MenuActionSyncGpx(
null);
        menuActionSyncGpx.act();
        verifyAll();
    }

    @Test
    public void testAbort() {
        expectLastCall().times(2);

        replayAll();
        new MenuActionSyncGpx(null).abort();
        verifyAll();
    }
}
