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

import com.google.code.geobeagle.activity.cachelist.GpxImporterFactory;
import com.google.code.geobeagle.activity.cachelist.actions.menu.Abortable;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxImporter;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionSyncGpxTest {
    @Test
    public void testAct() {
        GpxImporter gpxImporter = PowerMock.createMock(GpxImporter.class);
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        GpxImporterFactory gpxImporterFactory = PowerMock.createMock(GpxImporterFactory.class);
        CacheWriter cacheWriter = PowerMock.createMock(CacheWriter.class);

        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        expect(dbFrontend.getCacheWriter()).andReturn(cacheWriter);

        EasyMock.expect(gpxImporterFactory.create(cacheWriter)).andReturn(gpxImporter);
        gpxImporter.importGpxs(cacheListRefresh);

        PowerMock.replayAll();
        final MenuActionSyncGpx menuActionSyncGpx = new MenuActionSyncGpx(null, cacheListRefresh,
                gpxImporterFactory, dbFrontend);
        menuActionSyncGpx.act();
        PowerMock.verifyAll();
    }

    @Test
    public void testAbort() {
        Abortable abortable = PowerMock.createMock(Abortable.class);

        abortable.abort();

        PowerMock.replayAll();
        new MenuActionSyncGpx(abortable, null, null, null).abort();
        PowerMock.verifyAll();
    }
}
